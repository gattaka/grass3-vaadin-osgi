package cz.gattserver.grass3.recipes.web;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.Grid.Column;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.textfield.TextFieldVariant;
import com.vaadin.flow.data.provider.CallbackDataProvider.CountCallback;
import com.vaadin.flow.data.provider.CallbackDataProvider.FetchCallback;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.router.Route;

import cz.gattserver.grass3.recipes.facades.RecipesService;
import cz.gattserver.grass3.recipes.model.dto.RecipeDTO;
import cz.gattserver.grass3.recipes.model.dto.RecipeOverviewTO;
import cz.gattserver.grass3.security.CoreRole;
import cz.gattserver.grass3.services.SecurityService;
import cz.gattserver.grass3.ui.components.button.CreateGridButton;
import cz.gattserver.grass3.ui.components.button.ModifyGridButton;
import cz.gattserver.grass3.ui.pages.template.OneColumnPage;
import cz.gattserver.web.common.spring.SpringContextHelper;
import cz.gattserver.web.common.ui.HtmlDiv;

@Route("recipes")
public class RecipesPage extends OneColumnPage {

	private static final long serialVersionUID = 1214280599196303350L;

	@Autowired
	private SecurityService securityService;

	private transient RecipesService recipesService;

	private Grid<RecipeOverviewTO> grid;
	private H2 nameLabel;
	private HtmlDiv contentLabel;
	private RecipeDTO choosenRecipe;
	private RecipeOverviewTO filterTO;

	public RecipesPage() {
		init();
	}

	private void showDetail(RecipeDTO choosenRecipe) {
		nameLabel.setText(choosenRecipe.getName());
		contentLabel.setText(getRecipesService().eolToBreakline(choosenRecipe.getDescription()));
		this.choosenRecipe = choosenRecipe;
	}

	@Override
	protected void createColumnContent(Div layout) {
		HorizontalLayout recipesLayout = new HorizontalLayout();
		recipesLayout.setWidth("100%");
		layout.add(recipesLayout);

		filterTO = new RecipeOverviewTO();

		grid = new Grid<>();
		Column<RecipeOverviewTO> nazevColumn = grid.addColumn(RecipeOverviewTO::getName).setHeader("Název");
		grid.setWidth("358px");
		grid.setHeight("600px");
		recipesLayout.add(grid);

		grid.addSelectionListener((e) -> e.getFirstSelectedItem()
				.ifPresent((v) -> showDetail(getRecipesService().getRecipeById(v.getId()))));

		HeaderRow filteringHeader = grid.appendHeaderRow();

		// Název
		TextField nazevColumnField = new TextField();
		nazevColumnField.addThemeVariants(TextFieldVariant.LUMO_SMALL);
		nazevColumnField.setWidth("100%");
		nazevColumnField.addValueChangeListener(e -> {
			filterTO.setName(e.getValue());
			populate();
		});
		filteringHeader.getCell(nazevColumn).setComponent(nazevColumnField);

		populate();

		VerticalLayout contentLayout = new VerticalLayout();

		nameLabel = new H2();
		contentLayout.add(nameLabel);

		contentLabel = new HtmlDiv();
		contentLabel.setWidth("100%");
		contentLayout.add(contentLabel);

		HorizontalLayout btnLayout = new HorizontalLayout();
		btnLayout.setSpacing(true);
		layout.add(btnLayout);

		btnLayout.setVisible(securityService.getCurrentUser().getRoles().contains(CoreRole.ADMIN));

		btnLayout.add(new CreateGridButton("Přidat", event -> {
			new RecipeWindow() {
				private static final long serialVersionUID = -4863260002363608014L;

				@Override
				protected void onSave(String name, String desc, Long id) {
					id = getRecipesService().saveRecipe(name, desc);
					RecipeDTO to = new RecipeDTO(id, name, desc);
					showDetail(to);
					populate();
				}
			}.open();
		}));

		btnLayout.add(new ModifyGridButton<RecipeOverviewTO>("Upravit", event -> {
			new RecipeWindow(choosenRecipe) {
				private static final long serialVersionUID = 5264621441522056786L;

				@Override
				protected void onSave(String name, String desc, Long id) {
					getRecipesService().saveRecipe(name, desc, id);
					RecipeDTO to = new RecipeDTO(id, name, desc);
					showDetail(to);
					populate();
				}
			}.open();
		}, grid));
	}

	private RecipesService getRecipesService() {
		if (recipesService == null)
			recipesService = SpringContextHelper.getBean(RecipesService.class);
		return recipesService;
	}

	private void populate() {
		FetchCallback<RecipeOverviewTO, Void> fetchCallback = q -> getRecipesService()
				.getRecipes(filterTO.getName(), q.getOffset(), q.getLimit()).stream();
		CountCallback<RecipeOverviewTO, Void> countCallback = q -> getRecipesService()
				.getRecipesCount(filterTO.getName());
		grid.setDataProvider(DataProvider.fromCallbacks(fetchCallback, countCallback));
	}
}
