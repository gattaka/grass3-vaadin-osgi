package cz.gattserver.grass3.drinks.ui;

import java.util.Arrays;

import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.Grid.Column;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.textfield.TextFieldVariant;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.provider.CallbackDataProvider.CountCallback;
import com.vaadin.flow.data.provider.CallbackDataProvider.FetchCallback;
import com.vaadin.flow.data.renderer.TextRenderer;

import cz.gattserver.grass3.drinks.model.domain.WineType;
import cz.gattserver.grass3.drinks.model.interfaces.WineOverviewTO;
import cz.gattserver.grass3.drinks.model.interfaces.WineTO;
import cz.gattserver.grass3.ui.components.button.CreateGridButton;
import cz.gattserver.grass3.ui.components.button.DeleteGridButton;
import cz.gattserver.grass3.ui.components.button.ModifyGridButton;
import cz.gattserver.grass3.ui.util.RatingStars;
import cz.gattserver.web.common.ui.HtmlDiv;
import cz.gattserver.web.common.ui.Strong;

public class WineTab extends DrinksTab<WineTO, WineOverviewTO> {

	private static final long serialVersionUID = -8540314953045422691L;

	@Override
	protected WineOverviewTO createNewOverviewTO() {
		return new WineOverviewTO();
	}

	@Override
	protected Grid<WineOverviewTO> createGrid(final WineOverviewTO filterTO) {

		final Grid<WineOverviewTO> grid = new Grid<>();
		HeaderRow filteringHeader = grid.appendHeaderRow();

		Column<WineOverviewTO> wineryColumn = grid.addColumn(WineOverviewTO::getWinery).setHeader("Vinařství")
				.setSortProperty("winery");

		addNameColumn(grid, filteringHeader);
		addCountryColumn(grid, filteringHeader);
		addAlcoholColumn(grid, filteringHeader);

		Column<WineOverviewTO> yearsColumn = grid.addColumn(WineOverviewTO::getYear).setHeader("Rok").setWidth("90px")
				.setFlexGrow(0).setSortProperty("year");

		TextField yearsColumnField = new TextField();
		yearsColumnField.addThemeVariants(TextFieldVariant.LUMO_SMALL);
		yearsColumnField.setWidth("100%");
		yearsColumnField.addValueChangeListener(e -> {
			filterTO.setYear(Integer.parseInt(e.getValue()));
			populate();
		});
		filteringHeader.getCell(yearsColumn).setComponent(yearsColumnField);

		Column<WineOverviewTO> wineTypeColumn = grid.addColumn(new TextRenderer<>(to -> to.getWineType().getCaption()))
				.setHeader("Typ vína").setWidth("100px").setFlexGrow(0).setSortProperty("wineType");

		addRatingStarsColumn(grid);

		grid.setWidth("100%");
		grid.setHeight("400px");

		add(grid);

		// Vinařství
		TextField wineryColumnField = new TextField();
		wineryColumnField.addThemeVariants(TextFieldVariant.LUMO_SMALL);
		wineryColumnField.setWidth("100%");
		wineryColumnField.addValueChangeListener(e -> {
			filterTO.setWinery(e.getValue());
			populate();
		});
		filteringHeader.getCell(wineryColumn).setComponent(wineryColumnField);

		// Typ vína
		ComboBox<WineType> typeColumnField = new ComboBox<>(null, Arrays.asList(WineType.values()));
		typeColumnField.setWidth("100%");
		typeColumnField.addValueChangeListener(e -> {
			filterTO.setWineType(e.getValue());
			populate();
		});
		typeColumnField.setItemLabelGenerator(WineType::getCaption);
		filteringHeader.getCell(wineTypeColumn).setComponent(typeColumnField);
		return grid;
	}

	@Override
	protected void populate() {
		FetchCallback<WineOverviewTO, WineOverviewTO> fetchCallback = q -> getDrinksFacade()
				.getWines(filterTO, q.getOffset(), q.getLimit(), q.getSortOrders()).stream();
		CountCallback<WineOverviewTO, WineOverviewTO> countCallback = q -> getDrinksFacade().countWines(filterTO);
		grid.setDataProvider(DataProvider.fromFilteringCallbacks(fetchCallback, countCallback));
	}

	@Override
	protected void populateBtnLayout(HorizontalLayout btnLayout) {
		btnLayout.add(new CreateGridButton("Přidat", event -> new WineWindow() {
			private static final long serialVersionUID = -4863260002363608014L;

			@Override
			protected void onSave(WineTO to) {
				to = getDrinksFacade().saveWine(to);
				showDetail(to);
				populate();
			}
		}.open()));

		btnLayout.add(new ModifyGridButton<WineOverviewTO>("Upravit", event -> new WineWindow(choosenDrink) {
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
	protected void populateDetail(VerticalLayout dataLayout) {
		H2 nameLabel = new H2(
				choosenDrink.getWinery() + " " + choosenDrink.getName() + " (" + choosenDrink.getCountry() + ")");
		dataLayout.add(nameLabel);

		RatingStars rs = new RatingStars();
		rs.setValue(choosenDrink.getRating());
		rs.setReadOnly(true);
		dataLayout.add(rs);

		FormLayout infoLayout = new FormLayout();
		dataLayout.add(infoLayout);

		infoLayout.add(new Strong("Rok"));
		infoLayout.add(choosenDrink.getYear() == null ? "" : String.valueOf(choosenDrink.getYear()));
		Strong b = new Strong("Alkohol (%)");
		infoLayout.add(b);
		b.setWidth("120px");
		infoLayout.add(choosenDrink.getAlcohol() == null ? "" : String.valueOf(choosenDrink.getAlcohol()));
		infoLayout.add(new Strong("Typ vína"));
		infoLayout.add(choosenDrink.getWineType().getCaption());

		HtmlDiv description = new HtmlDiv(choosenDrink.getDescription().replaceAll("\n", "<br/>"));
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
