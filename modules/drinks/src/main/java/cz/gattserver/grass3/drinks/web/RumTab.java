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
import cz.gattserver.grass3.drinks.model.domain.RumType;
import cz.gattserver.grass3.drinks.model.interfaces.RumOverviewTO;
import cz.gattserver.grass3.drinks.model.interfaces.RumTO;
import cz.gattserver.grass3.server.GrassRequest;
import cz.gattserver.grass3.ui.components.CreateGridButton;
import cz.gattserver.grass3.ui.components.DeleteGridButton;
import cz.gattserver.grass3.ui.components.ModifyGridButton;
import cz.gattserver.web.common.ui.BoldLabel;
import cz.gattserver.web.common.ui.H2Label;

public class RumTab extends DrinksTab<RumTO, RumOverviewTO> {

	private static final long serialVersionUID = 594189301140808163L;

	@Autowired
	private DrinksFacade drinksFacade;

	public RumTab(GrassRequest request) {
		super(request);
	}

	@Override
	protected RumOverviewTO createNewOverviewTO() {
		return new RumOverviewTO();
	}

	@Override
	protected Grid<RumOverviewTO> createGrid(final RumOverviewTO filterTO) {

		final Grid<RumOverviewTO> grid = new Grid<>();

		Column<RumOverviewTO, String> nameColumn = grid.addColumn(RumOverviewTO::getName).setCaption("Název")
				.setSortProperty("name");
		Column<RumOverviewTO, String> countryColumn = grid.addColumn(RumOverviewTO::getCountry).setCaption("Země")
				.setSortProperty("country");
		Column<RumOverviewTO, Double> alcoholColumn = grid.addColumn(RumOverviewTO::getAlcohol)
				.setRenderer(new NumberRenderer(NumberFormat.getNumberInstance(new Locale("cs", "CZ"))))
				.setCaption("Alkohol (%)").setWidth(80).setSortProperty("alcohol");
		Column<RumOverviewTO, Integer> yearsColumn = grid.addColumn(RumOverviewTO::getYears).setCaption("Stáří (roky)")
				.setWidth(90).setSortProperty("years");
		Column<RumOverviewTO, RumType> rumTypeColumn = grid.addColumn(RumOverviewTO::getRumType)
				.setRenderer(RumType::getCaption, new TextRenderer()).setCaption("Typ rumu").setWidth(100)
				.setSortProperty("rumType");
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

		// Typ rumu
		ComboBox<RumType> typeColumnField = new ComboBox<>(null, Arrays.asList(RumType.values()));
		typeColumnField.setWidth("100%");
		typeColumnField.addStyleName(ValoTheme.COMBOBOX_TINY);
		typeColumnField.addValueChangeListener(e -> {
			filterTO.setRumType(e.getValue());
			populate();
		});
		typeColumnField.setItemCaptionGenerator(RumType::getCaption);
		filteringHeader.getCell(rumTypeColumn).setComponent(typeColumnField);

		return grid;
	}

	@Override
	protected void populate() {
		grid.setDataProvider(
				(sortOrder, offset, limit) -> drinksFacade.getRums(filterTO, offset, limit, sortOrder).stream(),
				() -> drinksFacade.countRums(filterTO));
	}

	@Override
	protected void populateBtnLayout(HorizontalLayout btnLayout) {
		btnLayout.addComponent(new CreateGridButton("Přidat", event -> {
			UI.getCurrent().addWindow(new RumWindow() {
				private static final long serialVersionUID = -4863260002363608014L;

				@Override
				protected void onSave(RumTO to) {
					to = drinksFacade.saveRum(to);
					showDetail(to);
					populate();
				}
			});
		}));

		btnLayout.addComponent(new ModifyGridButton<RumOverviewTO>("Upravit", event -> {
			UI.getCurrent().addWindow(new RumWindow(choosenDrink) {

				private static final long serialVersionUID = 5264621441522056786L;

				@Override
				protected void onSave(RumTO to) {
					to = drinksFacade.saveRum(to);
					showDetail(to);
					populate();
				}

			});
		}, grid));

		btnLayout.addComponent(new DeleteGridButton<RumOverviewTO>("Smazat", items -> {
			for (RumOverviewTO s : items)
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
		dataLayout.addComponent(rs);

		GridLayout infoLayout = new GridLayout(2, 7);
		dataLayout.addComponent(infoLayout);

		BoldLabel b;
		infoLayout.addComponent(new BoldLabel("Stáří (roky)"));
		infoLayout.addComponent(
				new Label(choosenDrink.getYears() == null ? "" : String.valueOf(choosenDrink.getYears())));
		infoLayout.addComponent(b = new BoldLabel("Alkohol (%)"));
		b.setWidth("120px");
		infoLayout.addComponent(
				new Label(choosenDrink.getAlcohol() == null ? "" : String.valueOf(choosenDrink.getAlcohol())));
		infoLayout.addComponent(new BoldLabel("Typ rumu"));
		infoLayout.addComponent(new Label(choosenDrink.getRumType().getCaption()));

		Label descriptionLabel = new Label(choosenDrink.getDescription());
		descriptionLabel.setSizeFull();
		dataLayout.addComponent(descriptionLabel);
	}

	@Override
	protected String getURLPath() {
		return "rum";
	}

	@Override
	protected RumTO findById(Long id) {
		return drinksFacade.getRumById(id);
	}

}
