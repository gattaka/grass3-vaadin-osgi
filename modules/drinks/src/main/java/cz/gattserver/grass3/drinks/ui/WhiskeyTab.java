package cz.gattserver.grass3.drinks.ui;

import java.util.Arrays;

import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.Grid.Column;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.provider.CallbackDataProvider.CountCallback;
import com.vaadin.flow.data.provider.CallbackDataProvider.FetchCallback;
import com.vaadin.flow.data.renderer.TextRenderer;

import cz.gattserver.grass3.drinks.model.domain.WhiskeyType;
import cz.gattserver.grass3.drinks.model.interfaces.WhiskeyOverviewTO;
import cz.gattserver.grass3.drinks.model.interfaces.WhiskeyTO;
import cz.gattserver.grass3.ui.components.button.CreateGridButton;
import cz.gattserver.grass3.ui.components.button.DeleteGridButton;
import cz.gattserver.grass3.ui.components.button.ModifyGridButton;
import cz.gattserver.grass3.ui.util.ButtonLayout;
import cz.gattserver.grass3.ui.util.RatingStars;
import cz.gattserver.grass3.ui.util.UIUtils;
import cz.gattserver.grass3.ui.util.GridLayout;
import cz.gattserver.web.common.ui.HtmlDiv;

public class WhiskeyTab extends DrinksTab<WhiskeyTO, WhiskeyOverviewTO> {

	private static final long serialVersionUID = 594189301140808163L;

	@Override
	protected WhiskeyOverviewTO createNewOverviewTO() {
		return new WhiskeyOverviewTO();
	}

	@Override
	protected void configureGrid(Grid<WhiskeyOverviewTO> grid, final WhiskeyOverviewTO filterTO) {
		addNameColumn(grid);
		addCountryColumn(grid);
		addAlcoholColumn(grid);

		Column<WhiskeyOverviewTO> yearsColumn = grid.addColumn(WhiskeyOverviewTO::getYears).setHeader("Stáří (roky)")
				.setWidth("90px").setFlexGrow(0).setSortProperty("years");
		Column<WhiskeyOverviewTO> whiskeyTypeColumn = grid
				.addColumn(new TextRenderer<>(to -> to.getWhiskeyType().getCaption())).setHeader("Typ whiskey")
				.setWidth("150px").setFlexGrow(0).setSortProperty("whiskeyType");

		addRatingStarsColumn(grid);

		grid.setWidth("100%");
		grid.setHeight("400px");
		add(grid);

		// Stáří (roky)
		TextField yearsColumnField = UIUtils.asSmall(new TextField());
		yearsColumnField.setWidth("100%");
		yearsColumnField.addValueChangeListener(e -> {
			filterTO.setYears(Integer.parseInt(e.getValue()));
			populate();
		});
		getHeaderRow().getCell(yearsColumn).setComponent(yearsColumnField);

		// Typ Whiskeyu
		ComboBox<WhiskeyType> typeColumnField = UIUtils
				.asSmall(new ComboBox<>(null, Arrays.asList(WhiskeyType.values())));
		typeColumnField.setWidth("100%");
		typeColumnField.addValueChangeListener(e -> {
			filterTO.setWhiskeyType(e.getValue());
			populate();
		});
		typeColumnField.setItemLabelGenerator(WhiskeyType::getCaption);
		getHeaderRow().getCell(whiskeyTypeColumn).setComponent(typeColumnField);
	}

	@Override
	protected void populate() {
		FetchCallback<WhiskeyOverviewTO, WhiskeyOverviewTO> fetchCallback = q -> getDrinksFacade()
				.getWhiskeys(filterTO, q.getOffset(), q.getLimit(), q.getSortOrders()).stream();
		CountCallback<WhiskeyOverviewTO, WhiskeyOverviewTO> countCallback = q -> getDrinksFacade()
				.countWhiskeys(filterTO);
		grid.setDataProvider(DataProvider.fromFilteringCallbacks(fetchCallback, countCallback));
	}

	@Override
	protected void populateBtnLayout(ButtonLayout btnLayout) {
		btnLayout.add(new CreateGridButton("Přidat", event -> new WhiskeyDialog() {
			private static final long serialVersionUID = -4863260002363608014L;

			@Override
			protected void onSave(WhiskeyTO to) {
				to = getDrinksFacade().saveWhiskey(to);
				showDetail(to);
				populate();
			}
		}.open()));

		btnLayout.add(new ModifyGridButton<WhiskeyOverviewTO>("Upravit", event -> new WhiskeyDialog(choosenDrink) {
			private static final long serialVersionUID = 5264621441522056786L;

			@Override
			protected void onSave(WhiskeyTO to) {
				to = getDrinksFacade().saveWhiskey(to);
				showDetail(to);
				populate();
			}
		}.open(), grid));

		btnLayout.add(new DeleteGridButton<WhiskeyOverviewTO>("Smazat", items -> {
			for (WhiskeyOverviewTO s : items)
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
		tb.newRow().addStrong("Typ whiskey:").add(choosenDrink.getWhiskeyType().getCaption());

		tb.addClassName(UIUtils.TOP_MARGIN_CSS_CLASS);
		dataLayout.add(tb);

		HtmlDiv description = new HtmlDiv(choosenDrink.getDescription().replaceAll("\n", "<br/>"));
		description.addClassName(UIUtils.TOP_MARGIN_CSS_CLASS);
		description.setSizeFull();
		dataLayout.add(description);
	}

	@Override
	protected String getURLPath() {
		return "whiskey";
	}

	@Override
	protected WhiskeyTO findById(Long id) {
		return getDrinksFacade().getWhiskeyById(id);
	}

}
