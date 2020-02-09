package cz.gattserver.grass3.drinks.ui;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.Grid.Column;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.data.provider.CallbackDataProvider.CountCallback;
import com.vaadin.flow.data.provider.CallbackDataProvider.FetchCallback;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.renderer.TextRenderer;

import cz.gattserver.grass3.drinks.model.domain.WineType;
import cz.gattserver.grass3.drinks.model.interfaces.WineOverviewTO;
import cz.gattserver.grass3.drinks.model.interfaces.WineTO;
import cz.gattserver.grass3.ui.components.button.CreateGridButton;
import cz.gattserver.grass3.ui.components.button.DeleteGridButton;
import cz.gattserver.grass3.ui.components.button.ModifyGridButton;
import cz.gattserver.grass3.ui.util.ButtonLayout;
import cz.gattserver.grass3.ui.util.GridLayout;
import cz.gattserver.grass3.ui.util.RatingStars;
import cz.gattserver.grass3.ui.util.UIUtils;
import cz.gattserver.web.common.ui.HtmlDiv;

public class WineTab extends DrinksTab<WineTO, WineOverviewTO> {

	private static final long serialVersionUID = -8540314953045422691L;

	@Override
	protected WineOverviewTO createNewOverviewTO() {
		return new WineOverviewTO();
	}

	@Override
	protected void configureGrid(Grid<WineOverviewTO> grid, final WineOverviewTO filterTO) {
		Column<WineOverviewTO> wineryColumn = grid.addColumn(WineOverviewTO::getWinery).setHeader("Vinařství")
				.setSortProperty("winery");

		addNameColumn(grid);
		addCountryColumn(grid);
		addAlcoholColumn(grid);

		Column<WineOverviewTO> yearsColumn = grid.addColumn(WineOverviewTO::getYear).setHeader("Rok").setWidth("90px")
				.setFlexGrow(0).setSortProperty("year");

		// Rok
		UIUtils.addHeaderTextField(getHeaderRow().getCell(yearsColumn), e -> {
			filterTO.setYear(Integer.parseInt(e.getValue()));
			populate();
		});

		Column<WineOverviewTO> wineTypeColumn = grid.addColumn(new TextRenderer<>(to -> to.getWineType().getCaption()))
				.setHeader("Typ vína").setWidth("100px").setFlexGrow(0).setSortProperty("wineType");

		addRatingStarsColumn(grid);

		grid.setWidthFull();
		grid.setHeight("400px");

		add(grid);

		// Vinařství
		UIUtils.addHeaderTextField(getHeaderRow().getCell(wineryColumn), e -> {
			filterTO.setWinery(e.getValue());
			populate();
		});

		// Typ vína
		UIUtils.addHeaderComboBox(getHeaderRow().getCell(wineTypeColumn), WineType.class, WineType::getCaption, e -> {
			filterTO.setWineType(e.getValue());
			populate();
		});
	}

	@Override
	protected void populate() {
		FetchCallback<WineOverviewTO, WineOverviewTO> fetchCallback = q -> getDrinksFacade()
				.getWines(filterTO, q.getOffset(), q.getLimit(), q.getSortOrders()).stream();
		CountCallback<WineOverviewTO, WineOverviewTO> countCallback = q -> getDrinksFacade().countWines(filterTO);
		grid.setDataProvider(DataProvider.fromFilteringCallbacks(fetchCallback, countCallback));
	}

	@Override
	protected void populateBtnLayout(ButtonLayout btnLayout) {
		btnLayout.add(new CreateGridButton("Přidat", event -> new WineDialog() {
			private static final long serialVersionUID = -4863260002363608014L;

			@Override
			protected void onSave(WineTO to) {
				to = getDrinksFacade().saveWine(to);
				showDetail(to);
				populate();
			}
		}.open()));

		btnLayout.add(new ModifyGridButton<WineOverviewTO>("Upravit", event -> new WineDialog(choosenDrink) {
			private static final long serialVersionUID = 5264621441522056786L;

			@Override
			protected void onSave(WineTO to) {
				to = getDrinksFacade().saveWine(to);
				showDetail(to);
				populate();
			}
		}.open(), grid));

		btnLayout.add(new DeleteGridButton<WineOverviewTO>("Smazat", items -> {
			for (WineOverviewTO s : items)
				getDrinksFacade().deleteDrink(s.getId());
			populate();
			showDetail(null);
		}, grid));
	}

	@Override
	protected void populateDetail(Div dataLayout) {
		H2 nameLabel = new H2(
				choosenDrink.getWinery() + " " + choosenDrink.getName() + " (" + choosenDrink.getCountry() + ")");
		dataLayout.add(nameLabel);

		RatingStars rs = new RatingStars();
		rs.setValue(choosenDrink.getRating());
		rs.setReadOnly(true);
		dataLayout.add(rs);

		GridLayout tb = new GridLayout();
		tb.addStrong("Rok:").add(choosenDrink.getYear() == null ? "" : String.valueOf(choosenDrink.getYear()));
		tb.newRow().addStrong("Alkohol (%):")
				.add(choosenDrink.getAlcohol() == null ? "" : String.valueOf(choosenDrink.getAlcohol()));
		tb.newRow().addStrong("Typ vína:").add(choosenDrink.getWineType().getCaption());

		tb.addClassName(UIUtils.TOP_MARGIN_CSS_CLASS);
		dataLayout.add(tb);

		HtmlDiv description = new HtmlDiv(choosenDrink.getDescription().replaceAll("\n", "<br/>"));
		description.addClassName(UIUtils.TOP_MARGIN_CSS_CLASS);
		description.setSizeFull();
		dataLayout.add(description);
	}

	@Override
	protected String getURLPath() {
		return "wine";
	}

	@Override
	protected WineTO findById(Long id) {
		return getDrinksFacade().getWineById(id);
	}

}
