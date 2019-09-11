package cz.gattserver.grass3.drinks.ui;

import java.util.Arrays;

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
import com.vaadin.ui.renderers.TextRenderer;
import com.vaadin.ui.themes.ValoTheme;

import cz.gattserver.grass3.drinks.model.domain.WineType;
import cz.gattserver.grass3.drinks.model.interfaces.WineOverviewTO;
import cz.gattserver.grass3.drinks.model.interfaces.WineTO;
import cz.gattserver.grass3.server.GrassRequest;
import cz.gattserver.grass3.ui.components.CreateGridButton;
import cz.gattserver.grass3.ui.components.DeleteGridButton;
import cz.gattserver.grass3.ui.components.ModifyGridButton;
import cz.gattserver.web.common.ui.BoldSpan;
import cz.gattserver.web.common.ui.H2Label;

public class WineTab extends DrinksTab<WineTO, WineOverviewTO> {

	private static final long serialVersionUID = -8540314953045422691L;

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
		HeaderRow filteringHeader = grid.appendHeaderRow();

		Column<WineOverviewTO, String> wineryColumn = grid.addColumn(WineOverviewTO::getWinery).setCaption("Vinařství")
				.setSortProperty("winery");

		addNameColumn(grid, filteringHeader);
		addCountryColumn(grid, filteringHeader);
		addAlcoholColumn(grid, filteringHeader);

		Column<WineOverviewTO, Integer> yearsColumn = grid.addColumn(WineOverviewTO::getYear).setCaption("Rok")
				.setWidth(90).setSortProperty("year");

		TextField yearsColumnField = new TextField();
		yearsColumnField.addStyleName(ValoTheme.TEXTFIELD_TINY);
		yearsColumnField.setWidth("100%");
		yearsColumnField.addValueChangeListener(e -> {
			filterTO.setYear(Integer.parseInt(e.getValue()));
			populate();
		});
		filteringHeader.getCell(yearsColumn).setComponent(yearsColumnField);

		Column<WineOverviewTO, WineType> wineTypeColumn = grid.addColumn(WineOverviewTO::getWineType)
				.setRenderer(WineType::getCaption, new TextRenderer()).setCaption("Typ vína").setWidth(100)
				.setSortProperty("wineType");

		addRatingStarsColumn(grid);

		grid.setWidth("100%");
		grid.setHeight("400px");

		addComponent(grid);

		// Vinařství
		TextField wineryColumnField = new TextField();
		wineryColumnField.addStyleName(ValoTheme.TEXTFIELD_TINY);
		wineryColumnField.setWidth("100%");
		wineryColumnField.addValueChangeListener(e -> {
			filterTO.setWinery(e.getValue());
			populate();
		});
		filteringHeader.getCell(wineryColumn).setComponent(wineryColumnField);

		// Typ vína
		ComboBox<WineType> typeColumnField = new ComboBox<>(null, Arrays.asList(WineType.values()));
		typeColumnField.setWidth("100%");
		typeColumnField.addStyleName(ValoTheme.COMBOBOX_TINY);
		typeColumnField.addValueChangeListener(e -> {
			filterTO.setWineType(e.getValue());
			populate();
		});
		typeColumnField.setItemCaptionGenerator(WineType::getCaption);
		filteringHeader.getCell(wineTypeColumn).setComponent(typeColumnField);
		return grid;
	}

	@Override
	protected void populate() {
		grid.setDataProvider(
				(sortOrder, offset, limit) -> getDrinksFacade().getWines(filterTO, offset, limit, sortOrder).stream(),
				() -> getDrinksFacade().countWines(filterTO));
	}

	@Override
	protected void populateBtnLayout(HorizontalLayout btnLayout) {
		btnLayout.addComponent(new CreateGridButton("Přidat", event -> UI.getCurrent().addWindow(new WineWindow() {
			private static final long serialVersionUID = -4863260002363608014L;

			@Override
			protected void onSave(WineTO to) {
				to = getDrinksFacade().saveWine(to);
				showDetail(to);
				populate();
			}
		})));

		btnLayout.addComponent(new ModifyGridButton<WineOverviewTO>("Upravit",
				event -> UI.getCurrent().addWindow(new WineWindow(choosenDrink) {
					private static final long serialVersionUID = 5264621441522056786L;

					@Override
					protected void onSave(WineTO to) {
						to = getDrinksFacade().saveWine(to);
						showDetail(to);
						populate();
					}
				}), grid));

		btnLayout.addComponent(new DeleteGridButton<WineOverviewTO>("Smazat", items -> {
			for (WineOverviewTO s : items)
				getDrinksFacade().deleteDrink(s.getId());
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
		rs.setAnimated(false);
		dataLayout.addComponent(rs);

		GridLayout infoLayout = new GridLayout(2, 7);
		dataLayout.addComponent(infoLayout);

		infoLayout.addComponent(new BoldSpan("Rok"));
		infoLayout
				.addComponent(new Label(choosenDrink.getYear() == null ? "" : String.valueOf(choosenDrink.getYear())));
		BoldSpan b = new BoldSpan("Alkohol (%)");
		infoLayout.addComponent(b);
		b.setWidth("120px");
		infoLayout.addComponent(
				new Label(choosenDrink.getAlcohol() == null ? "" : String.valueOf(choosenDrink.getAlcohol())));
		infoLayout.addComponent(new BoldSpan("Typ vína"));
		infoLayout.addComponent(new Label(choosenDrink.getWineType().getCaption()));

		Label descriptionLabel = new Label(choosenDrink.getDescription().replaceAll("\n", "<br/>"), ContentMode.HTML);
		descriptionLabel.setSizeFull();
		dataLayout.addComponent(descriptionLabel);
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
