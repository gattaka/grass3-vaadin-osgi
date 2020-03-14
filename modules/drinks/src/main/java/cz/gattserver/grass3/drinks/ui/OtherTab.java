package cz.gattserver.grass3.drinks.ui;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.Grid.Column;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.data.provider.CallbackDataProvider.CountCallback;
import com.vaadin.flow.data.provider.CallbackDataProvider.FetchCallback;
import com.vaadin.flow.data.provider.DataProvider;

import cz.gattserver.grass3.drinks.model.interfaces.OtherOverviewTO;
import cz.gattserver.grass3.drinks.model.interfaces.OtherTO;
import cz.gattserver.grass3.ui.components.button.CreateGridButton;
import cz.gattserver.grass3.ui.components.button.DeleteGridButton;
import cz.gattserver.grass3.ui.components.button.ModifyGridButton;
import cz.gattserver.grass3.ui.util.ButtonLayout;
import cz.gattserver.grass3.ui.util.GridLayout;
import cz.gattserver.grass3.ui.util.RatingStars;
import cz.gattserver.grass3.ui.util.UIUtils;
import cz.gattserver.web.common.ui.HtmlDiv;

public class OtherTab extends DrinksTab<OtherTO, OtherOverviewTO> {

	private static final long serialVersionUID = -8540314953045422691L;

	@Override
	protected OtherOverviewTO createNewOverviewTO() {
		return new OtherOverviewTO();
	}

	@Override
	protected void configureGrid(Grid<OtherOverviewTO> grid, final OtherOverviewTO filterTO) {
		Column<OtherOverviewTO> ingredientColumn = grid.addColumn(OtherOverviewTO::getIngredient)
				.setHeader("Ingredience").setSortProperty("winery");

		addNameColumn(grid);
		addCountryColumn(grid);
		addAlcoholColumn(grid);

		addRatingStarsColumn(grid);

		grid.setWidthFull();
		grid.setHeight("400px");

		add(grid);

		// Vinařství
		UIUtils.addHeaderTextField(getHeaderRow().getCell(ingredientColumn), e -> {
			filterTO.setIngredient(e.getValue());
			populate();
		});
	}

	@Override
	protected void populate() {
		FetchCallback<OtherOverviewTO, OtherOverviewTO> fetchCallback = q -> getDrinksFacade()
				.getOthers(filterTO, q.getOffset(), q.getLimit(), q.getSortOrders()).stream();
		CountCallback<OtherOverviewTO, OtherOverviewTO> countCallback = q -> getDrinksFacade().countOthers(filterTO);
		grid.setDataProvider(DataProvider.fromFilteringCallbacks(fetchCallback, countCallback));
	}

	@Override
	protected void populateBtnLayout(ButtonLayout btnLayout) {
		btnLayout.add(new CreateGridButton("Přidat", event -> new OtherDialog() {
			private static final long serialVersionUID = -4863260002363608014L;

			@Override
			protected void onSave(OtherTO to) {
				to = getDrinksFacade().saveOther(to);
				showDetail(to);
				populate();
			}
		}.open()));

		btnLayout.add(new ModifyGridButton<OtherOverviewTO>("Upravit", event -> new OtherDialog(choosenDrink) {
			private static final long serialVersionUID = 5264621441522056786L;

			@Override
			protected void onSave(OtherTO to) {
				to = getDrinksFacade().saveOther(to);
				showDetail(to);
				populate();
			}
		}.open(), grid));

		btnLayout.add(new DeleteGridButton<OtherOverviewTO>("Smazat", items -> {
			for (OtherOverviewTO s : items)
				getDrinksFacade().deleteDrink(s.getId());
			populate();
			showDetail(null);
		}, grid));
	}

	@Override
	protected void populateDetail(Div dataLayout) {
		H2 nameLabel = new H2(choosenDrink.getName() + " (" + choosenDrink.getCountry() + ")");
		dataLayout.add(nameLabel);

		RatingStars rs = new RatingStars();
		rs.setValue(choosenDrink.getRating());
		rs.setReadOnly(true);
		dataLayout.add(rs);

		GridLayout tb = new GridLayout();
		tb.addStrong("Ingredience:").add(choosenDrink.getIngredient());
		tb.newRow().addStrong("Alkohol (%):")
				.add(choosenDrink.getAlcohol() == null ? "" : String.valueOf(choosenDrink.getAlcohol()));

		tb.addClassName(UIUtils.TOP_MARGIN_CSS_CLASS);
		dataLayout.add(tb);

		HtmlDiv description = new HtmlDiv(choosenDrink.getDescription().replaceAll("\n", "<br/>"));
		description.addClassName(UIUtils.TOP_MARGIN_CSS_CLASS);
		description.setSizeFull();
		dataLayout.add(description);
	}

	@Override
	protected String getURLPath() {
		return "other";
	}

	@Override
	protected OtherTO findById(Long id) {
		return getDrinksFacade().getOtherById(id);
	}

}
