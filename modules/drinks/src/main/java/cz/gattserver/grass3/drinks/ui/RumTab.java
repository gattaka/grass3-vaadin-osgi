package cz.gattserver.grass3.drinks.ui;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.Grid.Column;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.data.provider.CallbackDataProvider.CountCallback;
import com.vaadin.flow.data.provider.CallbackDataProvider.FetchCallback;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.renderer.TextRenderer;

import cz.gattserver.grass3.drinks.model.domain.RumType;
import cz.gattserver.grass3.drinks.model.interfaces.RumOverviewTO;
import cz.gattserver.grass3.drinks.model.interfaces.RumTO;
import cz.gattserver.grass3.ui.components.button.CreateGridButton;
import cz.gattserver.grass3.ui.components.button.DeleteGridButton;
import cz.gattserver.grass3.ui.components.button.ModifyGridButton;
import cz.gattserver.grass3.ui.util.ButtonLayout;
import cz.gattserver.grass3.ui.util.GridLayout;
import cz.gattserver.grass3.ui.util.RatingStars;
import cz.gattserver.grass3.ui.util.UIUtils;
import cz.gattserver.web.common.ui.HtmlDiv;

public class RumTab extends DrinksTab<RumTO, RumOverviewTO> {

	private static final long serialVersionUID = 594189301140808163L;

	@Override
	protected RumOverviewTO createNewOverviewTO() {
		return new RumOverviewTO();
	}

	@Override
	protected void configureGrid(Grid<RumOverviewTO> grid, final RumOverviewTO filterTO) {
		addNameColumn(grid);
		addCountryColumn(grid);
		addAlcoholColumn(grid);

		Column<RumOverviewTO> yearsColumn = grid.addColumn(RumOverviewTO::getYears).setHeader("Stáří (roky)")
				.setWidth("90px").setFlexGrow(0).setSortProperty("years");
		Column<RumOverviewTO> rumTypeColumn = grid.addColumn(new TextRenderer<>(to -> to.getRumType().getCaption()))
				.setHeader("Typ rumu").setWidth("100px").setFlexGrow(0).setSortProperty("rumType");

		addRatingStarsColumn(grid);

		grid.setWidth("100%");
		grid.setHeight("400px");

		add(grid);

		// Stáří (roky)
		UIUtils.addHeaderTextField(getHeaderRow().getCell(yearsColumn), e -> {
			filterTO.setYears(Integer.parseInt(e.getValue()));
			populate();
		});

		// Typ rumu
		UIUtils.addHeaderComboBox(getHeaderRow().getCell(rumTypeColumn), RumType.class, RumType::getCaption, e -> {
			filterTO.setRumType(e.getValue());
			populate();
		});
	}

	@Override
	protected void populate() {
		FetchCallback<RumOverviewTO, RumOverviewTO> fetchCallback = q -> getDrinksFacade()
				.getRums(filterTO, q.getOffset(), q.getLimit(), q.getSortOrders()).stream();
		CountCallback<RumOverviewTO, RumOverviewTO> countCallback = q -> getDrinksFacade().countRums(filterTO);
		grid.setDataProvider(DataProvider.fromFilteringCallbacks(fetchCallback, countCallback));
	}

	@Override
	protected void populateBtnLayout(ButtonLayout btnLayout) {
		btnLayout.add(new CreateGridButton("Přidat", event -> new RumDialog() {
			private static final long serialVersionUID = -4863260002363608014L;

			@Override
			protected void onSave(RumTO to) {
				to = getDrinksFacade().saveRum(to);
				showDetail(to);
				populate();
			}
		}.open()));

		btnLayout.add(new ModifyGridButton<RumOverviewTO>("Upravit", event -> new RumDialog(choosenDrink) {
			private static final long serialVersionUID = 5264621441522056786L;

			@Override
			protected void onSave(RumTO to) {
				to = getDrinksFacade().saveRum(to);
				showDetail(to);
				populate();
			}
		}.open(), grid));

		btnLayout.add(new DeleteGridButton<RumOverviewTO>("Smazat", items -> {
			for (RumOverviewTO s : items)
				getDrinksFacade().deleteDrink(s.getId());
			populate();
			showDetail(null);
		}, grid));
	}

	@Override
	protected void populateDetail(Div dataLayout) {
		H2 nameLabel = new H2(choosenDrink.getName() + " (" + choosenDrink.getCountry() + ")");
		dataLayout.add(nameLabel);

		RatingStars rs = new RatingStars();
		rs.setValue(choosenDrink.getRating());
		rs.setReadOnly(true);
		dataLayout.add(rs);

		GridLayout tb = new GridLayout();
		tb.addStrong("Stáří (roky):")
				.add(choosenDrink.getYears() == null ? "" : String.valueOf(choosenDrink.getYears()));
		tb.newRow().addStrong("Alkohol (%):").add(String.valueOf(choosenDrink.getAlcohol()));
		tb.newRow().addStrong("Typ rumu:").add(choosenDrink.getRumType().getCaption());

		tb.addClassName(UIUtils.TOP_MARGIN_CSS_CLASS);
		dataLayout.add(tb);

		HtmlDiv description = new HtmlDiv(choosenDrink.getDescription().replaceAll("\n", "<br/>"));
		description.addClassName(UIUtils.TOP_MARGIN_CSS_CLASS);
		description.setSizeFull();
		dataLayout.add(description);
	}

	@Override
	protected String getURLPath() {
		return "rum";
	}

	@Override
	protected RumTO findById(Long id) {
		return getDrinksFacade().getRumById(id);
	}

}
