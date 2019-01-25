package cz.gattserver.grass3.drinks.ui;

import java.text.NumberFormat;
import java.util.Arrays;
import java.util.Locale;

import org.vaadin.teemu.ratingstars.RatingStars;

import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.Column;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.components.grid.HeaderRow;
import com.vaadin.ui.renderers.NumberRenderer;
import com.vaadin.ui.renderers.TextRenderer;
import com.vaadin.ui.themes.ValoTheme;

import cz.gattserver.grass3.drinks.model.domain.MaltType;
import cz.gattserver.grass3.drinks.model.interfaces.BeerOverviewTO;
import cz.gattserver.grass3.drinks.model.interfaces.BeerTO;
import cz.gattserver.grass3.server.GrassRequest;
import cz.gattserver.grass3.ui.components.CreateGridButton;
import cz.gattserver.grass3.ui.components.DeleteGridButton;
import cz.gattserver.grass3.ui.components.ModifyGridButton;
import cz.gattserver.web.common.ui.BoldLabel;
import cz.gattserver.web.common.ui.H2Label;

public class BeersTab extends DrinksTab<BeerTO, BeerOverviewTO> {

	private static final long serialVersionUID = 594189301140808163L;

	public BeersTab(GrassRequest request) {
		super(request);
	}

	@Override
	protected BeerOverviewTO createNewOverviewTO() {
		return new BeerOverviewTO();
	}

	@Override
	protected Grid<BeerOverviewTO> createGrid(final BeerOverviewTO filterTO) {

		final Grid<BeerOverviewTO> grid = new Grid<>();
		HeaderRow filteringHeader = grid.appendHeaderRow();

		Column<BeerOverviewTO, String> breweryColumn = grid.addColumn(BeerOverviewTO::getBrewery).setCaption("Pivovar")
				.setSortProperty("brewery");

		addNameColumn(grid, filteringHeader);

		Column<BeerOverviewTO, String> categoryColumn = grid.addColumn(BeerOverviewTO::getCategory)
				.setCaption("Kategorie").setWidth(80).setSortProperty("category");
		Column<BeerOverviewTO, Double> degreesColumn = grid.addColumn(BeerOverviewTO::getDegrees)
				.setRenderer(new NumberRenderer(NumberFormat.getNumberInstance(new Locale("cs", "CZ"))))
				.setCaption("Stupně (°)").setWidth(80).setSortProperty("degrees");

		addAlcoholColumn(grid, filteringHeader);

		Column<BeerOverviewTO, Integer> ibuColumn = grid.addColumn(BeerOverviewTO::getIbu).setCaption("Hořkost (IBU)")
				.setWidth(90).setSortProperty("ibu");
		Column<BeerOverviewTO, MaltType> maltTypeColumn = grid.addColumn(BeerOverviewTO::getMaltType)
				.setRenderer(MaltType::getCaption, new TextRenderer()).setCaption("Typ sladu").setWidth(100)
				.setSortProperty("maltType");

		addRatingStarsColumn(grid);

		grid.setWidth("100%");
		grid.setHeight("400px");
		addComponent(grid);

		// Pivovar
		TextField breweryColumnField = new TextField();
		breweryColumnField.addStyleName(ValoTheme.TEXTFIELD_TINY);
		breweryColumnField.setWidth("100%");
		breweryColumnField.addValueChangeListener(e -> {
			filterTO.setBrewery(e.getValue());
			populate();
		});
		filteringHeader.getCell(breweryColumn).setComponent(breweryColumnField);

		// Kategorie
		TextField categoryColumnField = new TextField();
		categoryColumnField.addStyleName(ValoTheme.TEXTFIELD_TINY);
		categoryColumnField.setWidth("100%");
		categoryColumnField.addValueChangeListener(e -> {
			filterTO.setCategory(e.getValue());
			populate();
		});
		filteringHeader.getCell(categoryColumn).setComponent(categoryColumnField);

		// Stupně
		TextField degreesColumnField = new TextField();
		degreesColumnField.addStyleName(ValoTheme.TEXTFIELD_TINY);
		degreesColumnField.setWidth("100%");
		degreesColumnField.addValueChangeListener(e -> {
			filterTO.setDegrees(Double.parseDouble(e.getValue()));
			populate();
		});
		filteringHeader.getCell(degreesColumn).setComponent(degreesColumnField);

		// Hořkost
		TextField ibuColumnField = new TextField();
		ibuColumnField.addStyleName(ValoTheme.TEXTFIELD_TINY);
		ibuColumnField.setWidth("100%");
		ibuColumnField.addValueChangeListener(e -> {
			filterTO.setIbu(Integer.parseInt(e.getValue()));
			populate();
		});
		filteringHeader.getCell(ibuColumn).setComponent(ibuColumnField);

		// Typ sladu
		ComboBox<MaltType> typeColumnField = new ComboBox<>(null, Arrays.asList(MaltType.values()));
		typeColumnField.setWidth("100%");
		typeColumnField.addStyleName(ValoTheme.COMBOBOX_TINY);
		typeColumnField.addValueChangeListener(e -> {
			filterTO.setMaltType(e.getValue());
			populate();
		});
		typeColumnField.setItemCaptionGenerator(MaltType::getCaption);
		filteringHeader.getCell(maltTypeColumn).setComponent(typeColumnField);

		return grid;
	}

	@Override
	protected void populate() {
		grid.setDataProvider(
				(sortOrder, offset, limit) -> getDrinksFacade().getBeers(filterTO, offset, limit, sortOrder).stream(),
				() -> getDrinksFacade().countBeers(filterTO));
	}

	@Override
	protected void populateBtnLayout(HorizontalLayout btnLayout) {
		btnLayout.addComponent(new CreateGridButton("Přidat", event -> UI.getCurrent().addWindow(new BeerWindow() {
			private static final long serialVersionUID = -4863260002363608014L;

			@Override
			protected void onSave(BeerTO to) {
				to = getDrinksFacade().saveBeer(to);
				showDetail(to);
				populate();
			}
		})));

		btnLayout.addComponent(new ModifyGridButton<BeerOverviewTO>("Upravit",
				event -> UI.getCurrent().addWindow(new BeerWindow(choosenDrink) {
					private static final long serialVersionUID = 5264621441522056786L;

					@Override
					protected void onSave(BeerTO to) {
						to = getDrinksFacade().saveBeer(to);
						showDetail(to);
						populate();
					}
				}), grid));

		btnLayout.addComponent(new DeleteGridButton<BeerOverviewTO>("Smazat", items -> {
			for (BeerOverviewTO s : items)
				getDrinksFacade().deleteDrink(s.getId());
			populate();
			showDetail(null);
		}, grid));
	}

	@Override
	protected void populateDetail(VerticalLayout dataLayout) {
		H2Label nameLabel = new H2Label(
				choosenDrink.getBrewery() + " " + choosenDrink.getName() + " (" + choosenDrink.getCountry() + ")");
		dataLayout.addComponent(nameLabel);

		RatingStars rs = new RatingStars();
		rs.setValue(choosenDrink.getRating());
		rs.setReadOnly(true);
		rs.setAnimated(false);
		dataLayout.addComponent(rs);

		GridLayout infoLayout = new GridLayout(2, 7);
		dataLayout.addComponent(infoLayout);

		BoldLabel b = new BoldLabel("Kategorie");
		infoLayout.addComponent(b);
		b.setWidth("120px");
		infoLayout.addComponent(new Label(choosenDrink.getCategory()));
		infoLayout.addComponent(new BoldLabel("Stupně (°)"));
		infoLayout.addComponent(new Label(String.valueOf(choosenDrink.getDegrees())));
		infoLayout.addComponent(new BoldLabel("Alkohol (%)"));
		infoLayout.addComponent(new Label(String.valueOf(choosenDrink.getAlcohol())));
		infoLayout.addComponent(new BoldLabel("Hořkost (IBU)"));
		infoLayout.addComponent(new Label(choosenDrink.getIbu() == null ? "" : String.valueOf(choosenDrink.getIbu())));
		infoLayout.addComponent(new BoldLabel("Typ sladu"));
		infoLayout.addComponent(new Label(choosenDrink.getMaltType().getCaption()));
		infoLayout.addComponent(new BoldLabel("Slady"));
		infoLayout.addComponent(new Label(choosenDrink.getMalts()));
		infoLayout.addComponent(new BoldLabel("Chmely"));
		infoLayout.addComponent(new Label(choosenDrink.getHops()));

		Label descriptionLabel = new Label(choosenDrink.getDescription().replaceAll("\n", "<br/>"), ContentMode.HTML);
		descriptionLabel.setSizeFull();
		dataLayout.addComponent(descriptionLabel);
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