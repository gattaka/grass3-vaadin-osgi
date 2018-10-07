package cz.gattserver.grass3.recipes.web;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.Component;
import com.vaadin.ui.Grid;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

import cz.gattserver.grass3.recipes.facades.RecipesFacade;
import cz.gattserver.grass3.recipes.model.dto.RecipeDTO;
import cz.gattserver.grass3.recipes.model.dto.RecipeOverviewDTO;
import cz.gattserver.grass3.security.CoreRole;
import cz.gattserver.grass3.server.GrassRequest;
import cz.gattserver.grass3.services.SecurityService;
import cz.gattserver.grass3.ui.components.CreateGridButton;
import cz.gattserver.grass3.ui.components.ModifyGridButton;
import cz.gattserver.grass3.ui.pages.template.OneColumnPage;
import cz.gattserver.web.common.ui.H2Label;

public class RecipesPage extends OneColumnPage {

	@Autowired
	private RecipesFacade recipesFacade;

	@Autowired
	private SecurityService securityService;

	private Label nameLabel;
	private Label contentLabel;
	private RecipeDTO choosenRecipe;
	private List<RecipeOverviewDTO> recipes;

	public RecipesPage(GrassRequest request) {
		super(request);
	}

	private void showDetail(RecipeDTO choosenRecipe) {
		nameLabel.setValue(choosenRecipe.getName());
		contentLabel.setValue(recipesFacade.eolToBreakline(choosenRecipe.getDescription()));
		this.choosenRecipe = choosenRecipe;
	}

	private void loadRecipes() {
		recipes = recipesFacade.getRecipes();
	}

	@Override
	protected Component createContent() {
		VerticalLayout marginLayout = new VerticalLayout();
		marginLayout.setMargin(true);

		VerticalLayout layout = new VerticalLayout();
		layout.setSpacing(true);
		layout.setMargin(true);
		marginLayout.addComponent(layout);

		HorizontalLayout recipesLayout = new HorizontalLayout();
		recipesLayout.setWidth("100%");
		layout.addComponent(recipesLayout);

		loadRecipes();
		Grid<RecipeOverviewDTO> grid = new Grid<>(null, recipes);
		grid.addColumn(RecipeOverviewDTO::getName).setCaption("Název");
		grid.setWidth("358px");
		grid.setHeight("600px");
		recipesLayout.addComponent(grid);

		grid.addSelectionListener(
				(e) -> e.getFirstSelectedItem().ifPresent((v) -> showDetail(recipesFacade.getRecipeById(v.getId()))));

		VerticalLayout contentLayout = new VerticalLayout();
		Panel panel = new Panel(contentLayout);
		panel.setSizeFull();
		recipesLayout.addComponent(panel);
		recipesLayout.setExpandRatio(panel, 1);

		nameLabel = new H2Label();
		contentLayout.addComponent(nameLabel);

		contentLabel = new Label();
		contentLabel.setWidth("100%");
		contentLabel.setContentMode(ContentMode.HTML);
		contentLayout.addComponent(contentLabel);

		HorizontalLayout btnLayout = new HorizontalLayout();
		btnLayout.setSpacing(true);
		layout.addComponent(btnLayout);

		btnLayout.setVisible(securityService.getCurrentUser().getRoles().contains(CoreRole.ADMIN));

		btnLayout.addComponent(new CreateGridButton("Přidat", event -> {
			UI.getCurrent().addWindow(new RecipeWindow() {
				private static final long serialVersionUID = -4863260002363608014L;

				@Override
				protected void onSave(String name, String desc, Long id) {
					id = recipesFacade.saveRecipe(name, desc);
					RecipeDTO to = new RecipeDTO(id, name, desc);
					showDetail(to);
					loadRecipes();
				}
			});
		}));

		btnLayout.addComponent(new ModifyGridButton<RecipeOverviewDTO>("Upravit", event -> {
			UI.getCurrent().addWindow(new RecipeWindow(choosenRecipe) {
				private static final long serialVersionUID = 5264621441522056786L;

				@Override
				protected void onSave(String name, String desc, Long id) {
					recipesFacade.saveRecipe(name, desc, id);
					RecipeDTO to = new RecipeDTO(id, name, desc);
					showDetail(to);
					loadRecipes();
				}
			});
		}, grid));

		return marginLayout;
	}
}
