package cz.gattserver.grass3.drinks.ui;

import java.text.NumberFormat;
import java.util.Arrays;
import java.util.Locale;

import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.Grid.Column;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.textfield.TextFieldVariant;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.provider.CallbackDataProvider.CountCallback;
import com.vaadin.flow.data.provider.CallbackDataProvider.FetchCallback;
import com.vaadin.flow.data.renderer.NumberRenderer;
import com.vaadin.flow.data.renderer.TextRenderer;

import cz.gattserver.grass3.drinks.model.domain.MaltType;
import cz.gattserver.grass3.drinks.model.interfaces.BeerOverviewTO;
import cz.gattserver.grass3.drinks.model.interfaces.BeerTO;
import cz.gattserver.grass3.ui.components.button.CreateGridButton;
import cz.gattserver.grass3.ui.components.button.DeleteGridButton;
import cz.gattserver.grass3.ui.components.button.ModifyGridButton;
import cz.gattserver.grass3.ui.util.RatingStars;
import cz.gattserver.web.common.ui.Breakline;
import cz.gattserver.web.common.ui.HtmlDiv;
import cz.gattserver.web.common.ui.Strong;

public class BeersTab extends DrinksTab<BeerTO, BeerOverviewTO> {

	private static final long serialVersionUID = 594189301140808163L;

	@Override
	protected BeerOverviewTO createNewOverviewTO() {
		return new BeerOverviewTO();
	}

	@Override
	protected Grid<BeerOverviewTO> createGrid(final BeerOverviewTO filterTO) {

		final Grid<BeerOverviewTO> grid = new Grid<>();
		HeaderRow filteringHeader = grid.appendHeaderRow();

		Column<BeerOverviewTO> breweryColumn = grid.addColumn(BeerOverviewTO::getBrewery).setHeader("Pivovar")
				.setSortProperty("brewery");

		addNameColumn(grid, filteringHeader);

		Column<BeerOverviewTO> categoryColumn = grid.addColumn(BeerOverviewTO::getCategory).setHeader("Kategorie")
				.setWidth("80px").setFlexGrow(0).setSortProperty("category");
		Column<BeerOverviewTO> degreesColumn = grid
				.addColumn(new NumberRenderer<BeerOverviewTO>(BeerOverviewTO::getDegrees,
						NumberFormat.getNumberInstance(new Locale("cs", "CZ"))))
				.setHeader("Stupně (°)").setWidth("80px").setFlexGrow(0).setSortProperty("degrees");

		addAlcoholColumn(grid, filteringHeader);

		Column<BeerOverviewTO> ibuColumn = grid.addColumn(BeerOverviewTO::getIbu).setHeader("Hořkost (IBU)")
				.setWidth("90px").setFlexGrow(0).setSortProperty("ibu");
		Column<BeerOverviewTO> maltTypeColumn = grid
				.addColumn(new TextRenderer<BeerOverviewTO>(to -> to.getMaltType().getCaption())).setHeader("Typ sladu")
				.setWidth("100px").setFlexGrow(0).setSortProperty("maltType");

		addRatingStarsColumn(grid);

		grid.setWidth("100%");
		grid.setHeight("400px");
		add(grid);

		// Pivovar
		TextField breweryColumnField = new TextField();
		breweryColumnField.addThemeVariants(TextFieldVariant.LUMO_SMALL);
		breweryColumnField.setWidth("100%");
		breweryColumnField.addValueChangeListener(e -> {
			filterTO.setBrewery(e.getValue());
			populate();
		});
		filteringHeader.getCell(breweryColumn).setComponent(breweryColumnField);

		// Kategorie
		TextField categoryColumnField = new TextField();
		categoryColumnField.addThemeVariants(TextFieldVariant.LUMO_SMALL);
		categoryColumnField.setWidth("100%");
		categoryColumnField.addValueChangeListener(e -> {
			filterTO.setCategory(e.getValue());
			populate();
		});
		filteringHeader.getCell(categoryColumn).setComponent(categoryColumnField);

		// Stupně
		TextField degreesColumnField = new TextField();
		degreesColumnField.addThemeVariants(TextFieldVariant.LUMO_SMALL);
		degreesColumnField.setWidth("100%");
		degreesColumnField.addValueChangeListener(e -> {
			filterTO.setDegrees(Double.parseDouble(e.getValue()));
			populate();
		});
		filteringHeader.getCell(degreesColumn).setComponent(degreesColumnField);

		// Hořkost
		TextField ibuColumnField = new TextField();
		ibuColumnField.addThemeVariants(TextFieldVariant.LUMO_SMALL);
		ibuColumnField.setWidth("100%");
		ibuColumnField.addValueChangeListener(e -> {
			filterTO.setIbu(Integer.parseInt(e.getValue()));
			populate();
		});
		filteringHeader.getCell(ibuColumn).setComponent(ibuColumnField);

		// Typ sladu
		ComboBox<MaltType> typeColumnField = new ComboBox<>(null, Arrays.asList(MaltType.values()));
		typeColumnField.setWidth("100%");
		typeColumnField.addValueChangeListener(e -> {
			filterTO.setMaltType(e.getValue());
			populate();
		});
		typeColumnField.setItemLabelGenerator(MaltType::getCaption);
		filteringHeader.getCell(maltTypeColumn).setComponent(typeColumnField);

		return grid;
	}

	@Override
	protected void populate() {
		FetchCallback<BeerOverviewTO, BeerOverviewTO> fetchCallback = q -> getDrinksFacade()
				.getBeers(filterTO, q.getOffset(), q.getLimit(), q.getSortOrders()).stream();
		CountCallback<BeerOverviewTO, BeerOverviewTO> countCallback = q -> getDrinksFacade().countBeers(filterTO);
		grid.setDataProvider(DataProvider.fromFilteringCallbacks(fetchCallback, countCallback));
	}

	@Override
	protected void populateBtnLayout(HorizontalLayout btnLayout) {
		btnLayout.add(new CreateGridButton("Přidat", event -> new BeerWindow() {
			private static final long serialVersionUID = -4863260002363608014L;

			@Override
			protected void onSave(BeerTO to) {
				to = getDrinksFacade().saveBeer(to);
				showDetail(to);
				populate();
			}
		}.open()));

		btnLayout.add(new ModifyGridButton<BeerOverviewTO>("Upravit", event -> new BeerWindow(choosenDrink) {
			private static final long serialVersionUID = 5264621441522056786L;

			@Override
			protected void onSave(BeerTO to) {
				to = getDrinksFacade().saveBeer(to);
				showDetail(to);
				populate();
			}
		}.open(), grid));

		btnLayout.add(new DeleteGridButton<BeerOverviewTO>("Smazat", items -> {
			for (BeerOverviewTO s : items)
				getDrinksFacade().deleteDrink(s.getId());
			populate();
			showDetail(null);
		}, grid));
	}

	@Override
	protected void populateDetail(VerticalLayout dataLayout) {
		H2 nameLabel = new H2(
				choosenDrink.getBrewery() + " " + choosenDrink.getName() + " (" + choosenDrink.getCountry() + ")");
		dataLayout.add(nameLabel);

		RatingStars rs = new RatingStars();
		rs.setValue(choosenDrink.getRating());
		rs.setReadOnly(true);
		dataLayout.add(rs);

		Div infoLayout = new Div();
		infoLayout.addClassName("top-margin");
		dataLayout.add(infoLayout);

		infoLayout.add(new Strong("Kategorie"));
		infoLayout.add(new Breakline());
		infoLayout.add(choosenDrink.getCategory());
		infoLayout.add(new Breakline());
		infoLayout.add(new Breakline());
		infoLayout.add(new Strong("Stupně (°)"));
		infoLayout.add(new Breakline());
		infoLayout.add(String.valueOf(choosenDrink.getDegrees()));
		infoLayout.add(new Breakline());
		infoLayout.add(new Breakline());
		infoLayout.add(new Strong("Stupně (°)"));
		infoLayout.add(String.valueOf(choosenDrink.getDegrees()));
		infoLayout.add(new Strong("Alkohol (%)"));
		infoLayout.add(String.valueOf(choosenDrink.getAlcohol()));
		infoLayout.add(new Strong("Hořkost (IBU)"));
		infoLayout.add(choosenDrink.getIbu() == null ? "" : String.valueOf(choosenDrink.getIbu()));
		infoLayout.add(new Strong("Typ sladu"));
		infoLayout.add(choosenDrink.getMaltType().getCaption());
		infoLayout.add(new Strong("Slady"));
		infoLayout.add(choosenDrink.getMalts());
		infoLayout.add(new Strong("Chmely"));
		infoLayout.add(choosenDrink.getHops());

		HtmlDiv description = new HtmlDiv(choosenDrink.getDescription().replaceAll("\n", "<br/>"));
		description.setSizeFull();
		dataLayout.add(description);
	}

	@Override
	protected String getURLPath() {
		return "beer";
	}

	@Override
	protected BeerTO findById(Long id) {
		return getDrinksFacade().getBeerById(id);
	}

}
