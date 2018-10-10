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
		HeaderRow filteringHeader = grid.appendHeaderRow();

		addNameColumn(grid, filteringHeader);
		addCountryColumn(grid, filteringHeader);
		addAlcoholColumn(grid, filteringHeader);

		Column<RumOverviewTO, Integer> yearsColumn = grid.addColumn(RumOverviewTO::getYears).setCaption("Stáří (roky)")
				.setWidth(90).setSortProperty("years");
		Column<RumOverviewTO, RumType> rumTypeColumn = grid.addColumn(RumOverviewTO::getRumType)
				.setRenderer(RumType::getCaption, new TextRenderer()).setCaption("Typ rumu").setWidth(100)
				.setSortProperty("rumType");

		addRatingStarsColumn(grid);

		grid.setWidth("100%");
		grid.setHeight("400px");

		addComponent(grid);

		// Stáří (roky)
		TextField yearsColumnField = new TextField();
		yearsColumnField.addStyleName(ValoTheme.TEXTFIELD_TINY);
		yearsColumnField.setWidth("100%");
		yearsColumnField.addValueChangeListener(e -> {
			filterTO.setYears(Integer.parseInt(e.getValue()));
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
				(sortOrder, offset, limit) -> getDrinksFacade().getRums(filterTO, offset, limit, sortOrder).stream(),
				() -> getDrinksFacade().countRums(filterTO));
	}

	@Override
	protected void populateBtnLayout(HorizontalLayout btnLayout) {
		btnLayout.addComponent(new CreateGridButton("Přidat", event -> UI.getCurrent().addWindow(new RumWindow() {
			private static final long serialVersionUID = -4863260002363608014L;

			@Override
			protected void onSave(RumTO to) {
				to = getDrinksFacade().saveRum(to);
				showDetail(to);
				populate();
			}
		})));

		btnLayout.addComponent(new ModifyGridButton<RumOverviewTO>("Upravit",
				event -> UI.getCurrent().addWindow(new RumWindow(choosenDrink) {

					private static final long serialVersionUID = 5264621441522056786L;

					@Override
					protected void onSave(RumTO to) {
						to = getDrinksFacade().saveRum(to);
						showDetail(to);
						populate();
					}

				}), grid));

		btnLayout.addComponent(new DeleteGridButton<RumOverviewTO>("Smazat", items -> {
			for (RumOverviewTO s : items)
				getDrinksFacade().deleteDrink(s.getId());
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

		infoLayout.addComponent(new BoldLabel("Stáří (roky)"));
		infoLayout.addComponent(
				new Label(choosenDrink.getYears() == null ? "" : String.valueOf(choosenDrink.getYears())));
		BoldLabel b = new BoldLabel("Alkohol (%)");
		infoLayout.addComponent(b);
		b.setWidth("120px");
		infoLayout.addComponent(
				new Label(choosenDrink.getAlcohol() == null ? "" : String.valueOf(choosenDrink.getAlcohol())));
		infoLayout.addComponent(new BoldLabel("Typ rumu"));
		infoLayout.addComponent(new Label(choosenDrink.getRumType().getCaption()));

		Label descriptionLabel = new Label(choosenDrink.getDescription().replaceAll("\n", "<br/>"), ContentMode.HTML);
		descriptionLabel.setSizeFull();
		dataLayout.addComponent(descriptionLabel);
	}

	@Override
	protected String getURLPath() {
		return "rum";
	}

	@Override
	protected RumTO findById(Long id) {
		return getDrinksFacade().getRumById(id);
	}

}
