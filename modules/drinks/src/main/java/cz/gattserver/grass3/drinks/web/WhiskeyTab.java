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
import cz.gattserver.grass3.drinks.model.domain.WhiskeyType;
import cz.gattserver.grass3.drinks.model.interfaces.WhiskeyOverviewTO;
import cz.gattserver.grass3.drinks.model.interfaces.WhiskeyTO;
import cz.gattserver.grass3.server.GrassRequest;
import cz.gattserver.grass3.ui.components.CreateGridButton;
import cz.gattserver.grass3.ui.components.DeleteGridButton;
import cz.gattserver.grass3.ui.components.ModifyGridButton;
import cz.gattserver.web.common.ui.BoldLabel;
import cz.gattserver.web.common.ui.H2Label;

public class WhiskeyTab extends DrinksTab<WhiskeyTO, WhiskeyOverviewTO> {

	private static final long serialVersionUID = 594189301140808163L;

	@Autowired
	private DrinksFacade drinksFacade;

	public WhiskeyTab(GrassRequest request) {
		super(request);
	}

	@Override
	protected WhiskeyOverviewTO createNewOverviewTO() {
		return new WhiskeyOverviewTO();
	}

	@Override
	protected Grid<WhiskeyOverviewTO> createGrid(final WhiskeyOverviewTO filterTO) {

		final Grid<WhiskeyOverviewTO> grid = new Grid<>();

		Column<WhiskeyOverviewTO, String> nameColumn = grid.addColumn(WhiskeyOverviewTO::getName).setCaption("Název")
				.setSortProperty("name");
		Column<WhiskeyOverviewTO, String> countryColumn = grid.addColumn(WhiskeyOverviewTO::getCountry)
				.setCaption("Země").setSortProperty("country");
		Column<WhiskeyOverviewTO, Double> alcoholColumn = grid.addColumn(WhiskeyOverviewTO::getAlcohol)
				.setRenderer(new NumberRenderer(NumberFormat.getNumberInstance(new Locale("cs", "CZ"))))
				.setCaption("Alkohol (%)").setWidth(80).setSortProperty("alcohol");
		Column<WhiskeyOverviewTO, Integer> yearsColumn = grid.addColumn(WhiskeyOverviewTO::getYears)
				.setCaption("Stáří (roky)").setWidth(90).setSortProperty("years");
		Column<WhiskeyOverviewTO, WhiskeyType> WhiskeyTypeColumn = grid.addColumn(WhiskeyOverviewTO::getWhiskeyType)
				.setRenderer(WhiskeyType::getCaption, new TextRenderer()).setCaption("Typ whiskey").setWidth(150)
				.setSortProperty("whiskeyType");
		grid.addColumn(to -> {
			RatingStars rs = new RatingStars();
			rs.setValue(to.getRating());
			rs.setReadOnly(true);
			rs.setAnimated(false);
			return rs;
		}).setRenderer(new ComponentRenderer()).setCaption("Hodnocení").setWidth(120).setSortProperty("rating");
		grid.setWidth("100%");
		grid.setHeight("400px");
		addComponent(grid);

		HeaderRow filteringHeader = grid.appendHeaderRow();

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

		// Stáří (roky)
		TextField yearsColumnField = new TextField();
		yearsColumnField.addStyleName(ValoTheme.TEXTFIELD_TINY);
		yearsColumnField.setWidth("100%");
		yearsColumnField.addValueChangeListener(e -> {
			try {
				filterTO.setYears(Integer.parseInt(e.getValue()));
			} catch (Exception ex) {
			}
			populate();
		});
		filteringHeader.getCell(yearsColumn).setComponent(yearsColumnField);

		// Typ Whiskeyu
		ComboBox<WhiskeyType> typeColumnField = new ComboBox<>(null, Arrays.asList(WhiskeyType.values()));
		typeColumnField.setWidth("100%");
		typeColumnField.addStyleName(ValoTheme.COMBOBOX_TINY);
		typeColumnField.addValueChangeListener(e -> {
			filterTO.setWhiskeyType(e.getValue());
			populate();
		});
		typeColumnField.setItemCaptionGenerator(WhiskeyType::getCaption);
		filteringHeader.getCell(WhiskeyTypeColumn).setComponent(typeColumnField);

		return grid;
	}

	@Override
	protected void populate() {
		grid.setDataProvider(
				(sortOrder, offset, limit) -> drinksFacade.getWhiskeys(filterTO, offset, limit, sortOrder).stream(),
				() -> drinksFacade.countWhiskeys(filterTO));
	}

	@Override
	protected void populateBtnLayout(HorizontalLayout btnLayout) {
		btnLayout.addComponent(new CreateGridButton("Přidat", event -> {
			UI.getCurrent().addWindow(new WhiskeyWindow() {
				private static final long serialVersionUID = -4863260002363608014L;

				@Override
				protected void onSave(WhiskeyTO to) {
					to = drinksFacade.saveWhiskey(to);
					showDetail(to);
					populate();
				}
			});
		}));

		btnLayout.addComponent(new ModifyGridButton<WhiskeyOverviewTO>("Upravit", event -> {
			UI.getCurrent().addWindow(new WhiskeyWindow(choosenDrink) {
				private static final long serialVersionUID = 5264621441522056786L;

				@Override
				protected void onSave(WhiskeyTO to) {
					to = drinksFacade.saveWhiskey(to);
					showDetail(to);
					populate();
				}
			});
		}, grid));

		btnLayout.addComponent(new DeleteGridButton<WhiskeyOverviewTO>("Smazat", items -> {
			for (WhiskeyOverviewTO s : items)
				drinksFacade.deleteDrink(s.getId());
			populate();
			showDetail(null);
		}, grid));
	}

	@Override
	protected void populateDetail(VerticalLayout dataLayout) {
		H2Label nameLabel = new H2Label(choosenDrink.getName() + " (" + choosenDrink.getCountry() + ")");
		dataLayout.addComponent(nameLabel);

		RatingStars rs = new RatingStars();
		rs.setValue(choosenDrink.getRating());
		rs.setReadOnly(true);
		rs.setAnimated(false);
		dataLayout.addComponent(rs);

		GridLayout infoLayout = new GridLayout(2, 7);
		dataLayout.addComponent(infoLayout);

		BoldLabel b;
		infoLayout.addComponent(new BoldLabel("Stáří (roky)"));
		infoLayout.addComponent(new Label(String.valueOf(choosenDrink.getYears())));
		infoLayout.addComponent(b = new BoldLabel("Alkohol (%)"));
		b.setWidth("120px");
		infoLayout.addComponent(new Label(String.valueOf(choosenDrink.getAlcohol())));
		infoLayout.addComponent(new BoldLabel("Typ whiskey"));
		infoLayout.addComponent(new Label(choosenDrink.getWhiskeyType().getCaption()));

		Label descriptionLabel = new Label(choosenDrink.getDescription());
		descriptionLabel.setSizeFull();
		dataLayout.addComponent(descriptionLabel);
	}

	@Override
	protected String getURLPath() {
		return "whiskey";
	}

	@Override
	protected WhiskeyTO findById(Long id) {
		return drinksFacade.getWhiskeyById(id);
	}

}
