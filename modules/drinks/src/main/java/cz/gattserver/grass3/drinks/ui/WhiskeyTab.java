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

import cz.gattserver.grass3.drinks.model.domain.WhiskeyType;
import cz.gattserver.grass3.drinks.model.interfaces.WhiskeyOverviewTO;
import cz.gattserver.grass3.drinks.model.interfaces.WhiskeyTO;
import cz.gattserver.grass3.server.GrassRequest;
import cz.gattserver.grass3.ui.components.CreateGridButton;
import cz.gattserver.grass3.ui.components.DeleteGridButton;
import cz.gattserver.grass3.ui.components.ModifyGridButton;
import cz.gattserver.web.common.ui.BoldSpan;
import cz.gattserver.web.common.ui.H2Label;

public class WhiskeyTab extends DrinksTab<WhiskeyTO, WhiskeyOverviewTO> {

	private static final long serialVersionUID = 594189301140808163L;

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
		HeaderRow filteringHeader = grid.appendHeaderRow();

		addNameColumn(grid, filteringHeader);
		addCountryColumn(grid, filteringHeader);
		addAlcoholColumn(grid, filteringHeader);

		Column<WhiskeyOverviewTO, Integer> yearsColumn = grid.addColumn(WhiskeyOverviewTO::getYears)
				.setCaption("Stáří (roky)").setWidth(90).setSortProperty("years");
		Column<WhiskeyOverviewTO, WhiskeyType> whiskeyTypeColumn = grid.addColumn(WhiskeyOverviewTO::getWhiskeyType)
				.setRenderer(WhiskeyType::getCaption, new TextRenderer()).setCaption("Typ whiskey").setWidth(150)
				.setSortProperty("whiskeyType");

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

		// Typ Whiskeyu
		ComboBox<WhiskeyType> typeColumnField = new ComboBox<>(null, Arrays.asList(WhiskeyType.values()));
		typeColumnField.setWidth("100%");
		typeColumnField.addStyleName(ValoTheme.COMBOBOX_TINY);
		typeColumnField.addValueChangeListener(e -> {
			filterTO.setWhiskeyType(e.getValue());
			populate();
		});
		typeColumnField.setItemCaptionGenerator(WhiskeyType::getCaption);
		filteringHeader.getCell(whiskeyTypeColumn).setComponent(typeColumnField);

		return grid;
	}

	@Override
	protected void populate() {
		grid.setDataProvider((sortOrder, offset, limit) -> getDrinksFacade()
				.getWhiskeys(filterTO, offset, limit, sortOrder).stream(),
				() -> getDrinksFacade().countWhiskeys(filterTO));
	}

	@Override
	protected void populateBtnLayout(HorizontalLayout btnLayout) {
		btnLayout.addComponent(new CreateGridButton("Přidat", event -> UI.getCurrent().addWindow(new WhiskeyWindow() {
			private static final long serialVersionUID = -4863260002363608014L;

			@Override
			protected void onSave(WhiskeyTO to) {
				to = getDrinksFacade().saveWhiskey(to);
				showDetail(to);
				populate();
			}
		})));

		btnLayout.addComponent(new ModifyGridButton<WhiskeyOverviewTO>("Upravit",
				event -> UI.getCurrent().addWindow(new WhiskeyWindow(choosenDrink) {
					private static final long serialVersionUID = 5264621441522056786L;

					@Override
					protected void onSave(WhiskeyTO to) {
						to = getDrinksFacade().saveWhiskey(to);
						showDetail(to);
						populate();
					}
				}), grid));

		btnLayout.addComponent(new DeleteGridButton<WhiskeyOverviewTO>("Smazat", items -> {
			for (WhiskeyOverviewTO s : items)
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

		infoLayout.addComponent(new BoldSpan("Stáří (roky)"));
		infoLayout.addComponent(new Label(String.valueOf(choosenDrink.getYears())));
		BoldSpan b = new BoldSpan("Alkohol (%)");
		infoLayout.addComponent(b);
		b.setWidth("120px");
		infoLayout.addComponent(new Label(String.valueOf(choosenDrink.getAlcohol())));
		infoLayout.addComponent(new BoldSpan("Typ whiskey"));
		infoLayout.addComponent(new Label(choosenDrink.getWhiskeyType().getCaption()));

		Label descriptionLabel = new Label(choosenDrink.getDescription().replaceAll("\n", "<br/>"), ContentMode.HTML);
		descriptionLabel.setSizeFull();
		dataLayout.addComponent(descriptionLabel);
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
