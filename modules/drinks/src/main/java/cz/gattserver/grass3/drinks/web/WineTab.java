package cz.gattserver.grass3.drinks.web;

import java.text.NumberFormat;
import java.util.Arrays;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.teemu.ratingstars.RatingStars;

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
import com.vaadin.ui.renderers.ComponentRenderer;
import com.vaadin.ui.renderers.NumberRenderer;
import com.vaadin.ui.renderers.TextRenderer;
import com.vaadin.ui.themes.ValoTheme;

import cz.gattserver.grass3.drinks.facades.DrinksFacade;
import cz.gattserver.grass3.drinks.model.domain.WineType;
import cz.gattserver.grass3.drinks.model.interfaces.WineOverviewTO;
import cz.gattserver.grass3.drinks.model.interfaces.WineTO;
import cz.gattserver.grass3.server.GrassRequest;
import cz.gattserver.grass3.ui.components.CreateGridButton;
import cz.gattserver.grass3.ui.components.DeleteGridButton;
import cz.gattserver.grass3.ui.components.ModifyGridButton;
import cz.gattserver.web.common.ui.BoldLabel;
import cz.gattserver.web.common.ui.H2Label;

public class WineTab extends DrinksTab<WineTO, WineOverviewTO> {

	private static final long serialVersionUID = -8540314953045422691L;

	@Autowired
	private DrinksFacade drinksFacade;

	public WineTab(GrassRequest request) {
		super(request);
	}

	@Override
	protected WineOverviewTO createNewOverviewTO() {
		return new WineOverviewTO();
	}

	@Override
	protected Grid<WineOverviewTO> createGrid(final WineOverviewTO filterTO) {
		final Grid<WineOverviewTO> grid = new Grid<>();

		Column<WineOverviewTO, String> wineryColumn = grid.addColumn(WineOverviewTO::getWinery).setCaption("Vinařství")
				.setSortProperty("winery");
		Column<WineOverviewTO, String> nameColumn = grid.addColumn(WineOverviewTO::getName).setCaption("Název")
				.setSortProperty("name");
		Column<WineOverviewTO, String> countryColumn = grid.addColumn(WineOverviewTO::getCountry).setCaption("Země")
				.setSortProperty("country");
		Column<WineOverviewTO, Double> alcoholColumn = grid.addColumn(WineOverviewTO::getAlcohol)
				.setRenderer(new NumberRenderer(NumberFormat.getNumberInstance(new Locale("cs", "CZ"))))
				.setCaption("Alkohol (%)").setWidth(80).setSortProperty("alcohol");
		Column<WineOverviewTO, Integer> yearsColumn = grid.addColumn(WineOverviewTO::getYear).setCaption("Rok")
				.setWidth(90).setSortProperty("year");
		Column<WineOverviewTO, WineType> WineTypeColumn = grid.addColumn(WineOverviewTO::getWineType)
				.setRenderer(WineType::getCaption, new TextRenderer()).setCaption("Typ vína").setWidth(100)
				.setSortProperty("wineType");
		grid.addColumn(to -> {
			RatingStars rs = new RatingStars();
			rs.setValue(to.getRating());
			rs.setReadOnly(true);
			return rs;
		}).setRenderer(new ComponentRenderer()).setCaption("Hodnocení").setWidth(120).setSortProperty("rating");
		grid.setWidth("100%");
		grid.setHeight("400px");

		addComponent(grid);

		HeaderRow filteringHeader = grid.appendHeaderRow();

		// Vinařství
		TextField wineryColumnField = new TextField();
		wineryColumnField.addStyleName(ValoTheme.TEXTFIELD_TINY);
		wineryColumnField.setWidth("100%");
		wineryColumnField.addValueChangeListener(e -> {
			filterTO.setWinery(e.getValue());
			populate();
		});
		filteringHeader.getCell(wineryColumn).setComponent(wineryColumnField);

		// Země původu
		TextField countryColumnField = new TextField();
		countryColumnField.addStyleName(ValoTheme.TEXTFIELD_TINY);
		countryColumnField.setWidth("100%");
		countryColumnField.addValueChangeListener(e -> {
			filterTO.setCountry(e.getValue());
			populate();
		});
		filteringHeader.getCell(countryColumn).setComponent(countryColumnField);

		// Název
		TextField nazevColumnField = new TextField();
		nazevColumnField.addStyleName(ValoTheme.TEXTFIELD_TINY);
		nazevColumnField.setWidth("100%");
		nazevColumnField.addValueChangeListener(e -> {
			filterTO.setName(e.getValue());
			populate();
		});
		filteringHeader.getCell(nameColumn).setComponent(nazevColumnField);

		// Alkohol
		TextField alcoholColumnField = new TextField();
		alcoholColumnField.addStyleName(ValoTheme.TEXTFIELD_TINY);
		alcoholColumnField.setWidth("100%");
		alcoholColumnField.addValueChangeListener(e -> {
			try {
				filterTO.setAlcohol(Double.parseDouble(e.getValue()));
			} catch (Exception ex) {
			}
			populate();
		});
		filteringHeader.getCell(alcoholColumn).setComponent(alcoholColumnField);

		// Rok
		TextField yearsColumnField = new TextField();
		yearsColumnField.addStyleName(ValoTheme.TEXTFIELD_TINY);
		yearsColumnField.setWidth("100%");
		yearsColumnField.addValueChangeListener(e -> {
			try {
				filterTO.setYear(Integer.parseInt(e.getValue()));
			} catch (Exception ex) {
			}
			populate();
		});
		filteringHeader.getCell(yearsColumn).setComponent(yearsColumnField);

		// Typ vína
		ComboBox<WineType> typeColumnField = new ComboBox<>(null, Arrays.asList(WineType.values()));
		typeColumnField.setWidth("100%");
		typeColumnField.addStyleName(ValoTheme.COMBOBOX_TINY);
		typeColumnField.addValueChangeListener(e -> {
			filterTO.setWineType(e.getValue());
			populate();
		});
		typeColumnField.setItemCaptionGenerator(WineType::getCaption);
		filteringHeader.getCell(WineTypeColumn).setComponent(typeColumnField);
		return grid;
	}

	@Override
	protected void populate() {
		grid.setDataProvider(
				(sortOrder, offset, limit) -> drinksFacade.getWines(filterTO, offset, limit, sortOrder).stream(),
				() -> drinksFacade.countWines(filterTO));
	}

	@Override
	protected void populateBtnLayout(HorizontalLayout btnLayout) {
		btnLayout.addComponent(new CreateGridButton("Přidat", event -> {
			UI.getCurrent().addWindow(new WineWindow() {
				private static final long serialVersionUID = -4863260002363608014L;

				@Override
				protected void onSave(WineTO to) {
					to = drinksFacade.saveWine(to);
					showDetail(to);
					populate();
				}
			});
		}));

		btnLayout.addComponent(new ModifyGridButton<WineOverviewTO>("Upravit", event -> {
			UI.getCurrent().addWindow(new WineWindow(choosenDrink) {

				private static final long serialVersionUID = 5264621441522056786L;

				@Override
				protected void onSave(WineTO to) {
					to = drinksFacade.saveWine(to);
					showDetail(to);
					populate();
				}

			});
		}, grid));

		btnLayout.addComponent(new DeleteGridButton<WineOverviewTO>("Smazat", items -> {
			for (WineOverviewTO s : items)
				drinksFacade.deleteDrink(s.getId());
			populate();
			showDetail(null);
		}, grid));
	}

	@Override
	protected void populateDetail(VerticalLayout dataLayout) {
		H2Label nameLabel = new H2Label(
				choosenDrink.getWinery() + " " + choosenDrink.getName() + " (" + choosenDrink.getCountry() + ")");
		dataLayout.addComponent(nameLabel);

		RatingStars rs = new RatingStars();
		rs.setValue(choosenDrink.getRating());
		rs.setReadOnly(true);
		dataLayout.addComponent(rs);

		GridLayout infoLayout = new GridLayout(2, 7);
		dataLayout.addComponent(infoLayout);

		BoldLabel b;
		infoLayout.addComponent(new BoldLabel("Rok"));
		infoLayout
				.addComponent(new Label(choosenDrink.getYear() == null ? "" : String.valueOf(choosenDrink.getYear())));
		infoLayout.addComponent(b = new BoldLabel("Alkohol (%)"));
		b.setWidth("120px");
		infoLayout.addComponent(
				new Label(choosenDrink.getAlcohol() == null ? "" : String.valueOf(choosenDrink.getAlcohol())));
		infoLayout.addComponent(new BoldLabel("Typ vína"));
		infoLayout.addComponent(new Label(choosenDrink.getWineType().getCaption()));

		Label descriptionLabel = new Label(choosenDrink.getDescription());
		descriptionLabel.setSizeFull();
		dataLayout.addComponent(descriptionLabel);
	}

	@Override
	protected String getURLPath() {
		return "wine";
	}

	@Override
	protected WineTO findById(Long id) {
		return drinksFacade.getWineById(id);
	}

}
