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

import cz.gattserver.grass3.drinks.model.domain.RumType;
import cz.gattserver.grass3.drinks.model.interfaces.RumOverviewTO;
import cz.gattserver.grass3.drinks.model.interfaces.RumTO;
import cz.gattserver.grass3.ui.components.button.CreateGridButton;
import cz.gattserver.grass3.ui.components.button.DeleteGridButton;
import cz.gattserver.grass3.ui.components.button.ModifyGridButton;
import cz.gattserver.grass3.ui.util.RatingStars;
import cz.gattserver.web.common.ui.HtmlDiv;
import cz.gattserver.web.common.ui.Strong;

public class RumTab extends DrinksTab<RumTO, RumOverviewTO> {

	private static final long serialVersionUID = 594189301140808163L;

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

		Column<RumOverviewTO> yearsColumn = grid.addColumn(RumOverviewTO::getYears).setHeader("Stáří (roky)")
				.setWidth("90px").setFlexGrow(0).setSortProperty("years");
		Column<RumOverviewTO> rumTypeColumn = grid.addColumn(new TextRenderer<>(to -> to.getRumType().getCaption()))
				.setHeader("Typ rumu").setWidth("100px").setFlexGrow(0).setSortProperty("rumType");

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

		// Typ rumu
		ComboBox<RumType> typeColumnField = new ComboBox<>(null, Arrays.asList(RumType.values()));
		typeColumnField.setWidth("100%");
		typeColumnField.addValueChangeListener(e -> {
			filterTO.setRumType(e.getValue());
			populate();
		});
		typeColumnField.setItemLabelGenerator(RumType::getCaption);
		filteringHeader.getCell(rumTypeColumn).setComponent(typeColumnField);

		return grid;
	}

	@Override
	protected void populate() {
		FetchCallback<RumOverviewTO, RumOverviewTO> fetchCallback = q -> getDrinksFacade()
				.getRums(filterTO, q.getOffset(), q.getLimit(), q.getSortOrders()).stream();
		CountCallback<RumOverviewTO, RumOverviewTO> countCallback = q -> getDrinksFacade().countRums(filterTO);
		grid.setDataProvider(DataProvider.fromFilteringCallbacks(fetchCallback, countCallback));
	}

	@Override
	protected void populateBtnLayout(HorizontalLayout btnLayout) {
		btnLayout.add(new CreateGridButton("Přidat", event -> new RumWindow() {
			private static final long serialVersionUID = -4863260002363608014L;

			@Override
			protected void onSave(RumTO to) {
				to = getDrinksFacade().saveRum(to);
				showDetail(to);
				populate();
			}
		}.open()));

		btnLayout.add(new ModifyGridButton<RumOverviewTO>("Upravit", event -> new RumWindow(choosenDrink) {
			private static final long serialVersionUID = 5264621441522056786L;

			@Override
			protected void onSave(RumTO to) {
				to = getDrinksFacade().saveRum(to);
				showDetail(to);
				populate();
			}
		}.open(), grid));

		btnLayout.add(new DeleteGridButton<RumOverviewTO>("Smazat", items -> {
			for (RumOverviewTO s : items)
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
		infoLayout.add(choosenDrink.getYears() == null ? "" : String.valueOf(choosenDrink.getYears()));
		Strong b = new Strong("Alkohol (%)");
		infoLayout.add(b);
		b.setWidth("120px");
		infoLayout.add(choosenDrink.getAlcohol() == null ? "" : String.valueOf(choosenDrink.getAlcohol()));
		infoLayout.add(new Strong("Typ rumu"));
		infoLayout.add(choosenDrink.getRumType().getCaption());

		HtmlDiv description = new HtmlDiv(choosenDrink.getDescription().replaceAll("\n", "<br/>"));
		description.setSizeFull();
		dataLayout.add(description);
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
