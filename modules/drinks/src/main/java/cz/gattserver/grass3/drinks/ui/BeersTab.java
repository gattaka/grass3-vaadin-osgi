package cz.gattserver.grass3.drinks.ui;

import java.text.NumberFormat;
import java.util.Locale;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.Grid.Column;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.data.provider.CallbackDataProvider.CountCallback;
import com.vaadin.flow.data.provider.CallbackDataProvider.FetchCallback;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.renderer.NumberRenderer;
import com.vaadin.flow.data.renderer.TextRenderer;

import cz.gattserver.grass3.drinks.model.domain.MaltType;
import cz.gattserver.grass3.drinks.model.interfaces.BeerOverviewTO;
import cz.gattserver.grass3.drinks.model.interfaces.BeerTO;
import cz.gattserver.grass3.ui.components.button.CreateGridButton;
import cz.gattserver.grass3.ui.components.button.DeleteGridButton;
import cz.gattserver.grass3.ui.components.button.ModifyGridButton;
import cz.gattserver.grass3.ui.util.ButtonLayout;
import cz.gattserver.grass3.ui.util.GridLayout;
import cz.gattserver.grass3.ui.util.RatingStars;
import cz.gattserver.grass3.ui.util.UIUtils;
import cz.gattserver.web.common.ui.HtmlDiv;

public class BeersTab extends DrinksTab<BeerTO, BeerOverviewTO> {

	private static final long serialVersionUID = 594189301140808163L;

	@Override
	protected BeerOverviewTO createNewOverviewTO() {
		return new BeerOverviewTO();
	}

	@Override
	protected void configureGrid(Grid<BeerOverviewTO> grid, final BeerOverviewTO filterTO) {
		Column<BeerOverviewTO> breweryColumn = grid.addColumn(BeerOverviewTO::getBrewery).setHeader("Pivovar")
				.setSortProperty("brewery");

		addNameColumn(grid);

		Column<BeerOverviewTO> categoryColumn = grid.addColumn(BeerOverviewTO::getCategory).setHeader("Kategorie")
				.setWidth("100px").setFlexGrow(0).setSortProperty("category");
		Column<BeerOverviewTO> degreesColumn = grid
				.addColumn(new NumberRenderer<BeerOverviewTO>(BeerOverviewTO::getDegrees,
						NumberFormat.getNumberInstance(new Locale("cs", "CZ"))))
				.setHeader("Stupně (°)").setWidth("100px").setFlexGrow(0).setSortProperty("degrees");

		addAlcoholColumn(grid);

		Column<BeerOverviewTO> ibuColumn = grid.addColumn(BeerOverviewTO::getIbu).setHeader("Hořkost (IBU)")
				.setWidth("120px").setFlexGrow(0).setSortProperty("ibu");
		Column<BeerOverviewTO> maltTypeColumn = grid
				.addColumn(new TextRenderer<BeerOverviewTO>(to -> to.getMaltType().getCaption())).setHeader("Typ sladu")
				.setWidth("150px").setFlexGrow(0).setSortProperty("maltType");

		addRatingStarsColumn(grid);

		grid.setWidthFull();
		grid.setHeight("400px");
		add(grid);

		// Pivovar
		UIUtils.addHeaderTextField(getHeaderRow().getCell(breweryColumn), e -> {
			filterTO.setBrewery(e.getValue());
			populate();
		});

		// Kategorie
		UIUtils.addHeaderTextField(getHeaderRow().getCell(categoryColumn), e -> {
			filterTO.setCategory(e.getValue());
			populate();
		});

		// Stupně
		UIUtils.addHeaderTextField(getHeaderRow().getCell(degreesColumn), e -> {
			filterTO.setDegrees(Double.parseDouble(e.getValue()));
			populate();
		});

		// Hořkost
		UIUtils.addHeaderTextField(getHeaderRow().getCell(ibuColumn), e -> {
			filterTO.setIbu(Integer.parseInt(e.getValue()));
			populate();
		});

		// Typ sladu
		UIUtils.addHeaderComboBox(getHeaderRow().getCell(maltTypeColumn), MaltType.class, MaltType::getCaption, e -> {
			filterTO.setMaltType(e.getValue());
			populate();
		});
	}

	@Override
	protected void populate() {
		FetchCallback<BeerOverviewTO, BeerOverviewTO> fetchCallback = q -> getDrinksFacade()
				.getBeers(filterTO, q.getOffset(), q.getLimit(), q.getSortOrders()).stream();
		CountCallback<BeerOverviewTO, BeerOverviewTO> countCallback = q -> getDrinksFacade().countBeers(filterTO);
		grid.setDataProvider(DataProvider.fromFilteringCallbacks(fetchCallback, countCallback));
	}

	@Override
	protected void populateBtnLayout(ButtonLayout btnLayout) {
		btnLayout.add(new CreateGridButton("Přidat", event -> new BeerDialog() {
			private static final long serialVersionUID = -4863260002363608014L;

			@Override
			protected void onSave(BeerTO to) {
				to = getDrinksFacade().saveBeer(to);
				showDetail(to);
				populate();
			}
		}.open()));

		btnLayout.add(new ModifyGridButton<BeerOverviewTO>("Upravit", event -> new BeerDialog(choosenDrink) {
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
	protected void populateDetail(Div dataLayout) {
		H2 nameLabel = new H2(
				choosenDrink.getBrewery() + " " + choosenDrink.getName() + " (" + choosenDrink.getCountry() + ")");
		dataLayout.add(nameLabel);

		RatingStars rs = new RatingStars();
		rs.setValue(choosenDrink.getRating());
		rs.setReadOnly(true);
		dataLayout.add(rs);

		GridLayout tb = new GridLayout();
		tb.addStrong("Kategorie:").add(choosenDrink.getCategory());
		tb.newRow().addStrong("Stupně (°):").add(String.valueOf(choosenDrink.getDegrees()));
		tb.addStrong("Alkohol (%):").add(String.valueOf(choosenDrink.getAlcohol()));
		tb.newRow().addStrong("Hořkost (IBU):")
				.add(choosenDrink.getIbu() == null ? "" : String.valueOf(choosenDrink.getIbu()));
		tb.addStrong("Typ sladu:").add(choosenDrink.getMaltType().getCaption());
		tb.newRow().addStrong("Slady (IBU):").add(choosenDrink.getMalts());
		tb.addStrong("Chmely:").add(choosenDrink.getHops());

		tb.addClassName(UIUtils.TOP_MARGIN_CSS_CLASS);
		dataLayout.add(tb);

		HtmlDiv description = new HtmlDiv(choosenDrink.getDescription().replaceAll("\n", "<br/>"));
		description.addClassName(UIUtils.TOP_MARGIN_CSS_CLASS);
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
