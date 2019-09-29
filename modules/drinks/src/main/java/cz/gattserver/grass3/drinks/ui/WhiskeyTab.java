package cz.gattserver.grass3.drinks.ui;

import java.util.Arrays;

import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.Grid.Column;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.textfield.TextFieldVariant;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.provider.CallbackDataProvider.CountCallback;
import com.vaadin.flow.data.provider.CallbackDataProvider.FetchCallback;
import com.vaadin.flow.data.renderer.TextRenderer;

import cz.gattserver.grass3.drinks.model.domain.WhiskeyType;
import cz.gattserver.grass3.drinks.model.interfaces.WhiskeyOverviewTO;
import cz.gattserver.grass3.drinks.model.interfaces.WhiskeyTO;
import cz.gattserver.grass3.ui.components.button.CreateGridButton;
import cz.gattserver.grass3.ui.components.button.DeleteGridButton;
import cz.gattserver.grass3.ui.components.button.ModifyGridButton;
import cz.gattserver.grass3.ui.util.RatingStars;
import cz.gattserver.web.common.ui.HtmlDiv;
import cz.gattserver.web.common.ui.Strong;

public class WhiskeyTab extends DrinksTab<WhiskeyTO, WhiskeyOverviewTO> {

	private static final long serialVersionUID = 594189301140808163L;

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

		Column<WhiskeyOverviewTO> yearsColumn = grid.addColumn(WhiskeyOverviewTO::getYears).setHeader("Stáří (roky)")
				.setWidth("90px").setFlexGrow(0).setSortProperty("years");
		Column<WhiskeyOverviewTO> whiskeyTypeColumn = grid
				.addColumn(new TextRenderer<>(to -> to.getWhiskeyType().getCaption())).setHeader("Typ whiskey")
				.setWidth("150px").setFlexGrow(0).setSortProperty("whiskeyType");

		addRatingStarsColumn(grid);

		grid.setWidth("100%");
		grid.setHeight("400px");
		add(grid);

		// Stáří (roky)
		TextField yearsColumnField = new TextField();
		yearsColumnField.addThemeVariants(TextFieldVariant.LUMO_SMALL);
		yearsColumnField.setWidth("100%");
		yearsColumnField.addValueChangeListener(e -> {
			filterTO.setYears(Integer.parseInt(e.getValue()));
			populate();
		});
		filteringHeader.getCell(yearsColumn).setComponent(yearsColumnField);

		// Typ Whiskeyu
		ComboBox<WhiskeyType> typeColumnField = new ComboBox<>(null, Arrays.asList(WhiskeyType.values()));
		typeColumnField.setWidth("100%");
		typeColumnField.addValueChangeListener(e -> {
			filterTO.setWhiskeyType(e.getValue());
			populate();
		});
		typeColumnField.setItemLabelGenerator(WhiskeyType::getCaption);
		filteringHeader.getCell(whiskeyTypeColumn).setComponent(typeColumnField);

		return grid;
	}

	@Override
	protected void populate() {
		FetchCallback<WhiskeyOverviewTO, WhiskeyOverviewTO> fetchCallback = q -> getDrinksFacade()
				.getWhiskeys(filterTO, q.getOffset(), q.getLimit(), q.getSortOrders()).stream();
		CountCallback<WhiskeyOverviewTO, WhiskeyOverviewTO> countCallback = q -> getDrinksFacade()
				.countWhiskeys(filterTO);
		grid.setDataProvider(DataProvider.fromFilteringCallbacks(fetchCallback, countCallback));
	}

	@Override
	protected void populateBtnLayout(HorizontalLayout btnLayout) {
		btnLayout.add(new CreateGridButton("Přidat", event -> new WhiskeyWindow() {
			private static final long serialVersionUID = -4863260002363608014L;

			@Override
			protected void onSave(WhiskeyTO to) {
				to = getDrinksFacade().saveWhiskey(to);
				showDetail(to);
				populate();
			}
		}.open()));

		btnLayout.add(new ModifyGridButton<WhiskeyOverviewTO>("Upravit", event -> new WhiskeyWindow(choosenDrink) {
			private static final long serialVersionUID = 5264621441522056786L;

			@Override
			protected void onSave(WhiskeyTO to) {
				to = getDrinksFacade().saveWhiskey(to);
				showDetail(to);
				populate();
			}
		}.open(), grid));

		btnLayout.add(new DeleteGridButton<WhiskeyOverviewTO>("Smazat", items -> {
			for (WhiskeyOverviewTO s : items)
				getDrinksFacade().deleteDrink(s.getId());
			populate();
			showDetail(null);
		}, grid));
	}

	@Override
	protected void populateDetail(VerticalLayout dataLayout) {
		H2 nameLabel = new H2(choosenDrink.getName() + " (" + choosenDrink.getCountry() + ")");
		dataLayout.add(nameLabel);

		RatingStars rs = new RatingStars();
		rs.setValue(choosenDrink.getRating());
		rs.setReadOnly(true);
		dataLayout.add(rs);

		FormLayout infoLayout = new FormLayout();
		dataLayout.add(infoLayout);

		infoLayout.add(new Strong("Stáří (roky)"));
		infoLayout.add(String.valueOf(choosenDrink.getYears()));
		Strong b = new Strong("Alkohol (%)");
		infoLayout.add(b);
		b.setWidth("120px");
		infoLayout.add(String.valueOf(choosenDrink.getAlcohol()));
		infoLayout.add(new Strong("Typ whiskey"));
		infoLayout.add(choosenDrink.getWhiskeyType().getCaption());

		HtmlDiv description = new HtmlDiv(choosenDrink.getDescription().replaceAll("\n", "<br/>"));
		description.setSizeFull();
		dataLayout.add(description);
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
