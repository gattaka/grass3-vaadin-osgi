package cz.gattserver.grass3.recipes.web;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.Reindeer;
import com.vaadin.ui.Button.ClickEvent;

import cz.gattserver.grass3.pages.template.OneColumnPage;
import cz.gattserver.grass3.recipes.facades.RecipesFacade;
import cz.gattserver.grass3.recipes.model.dto.RecipeDTO;
import cz.gattserver.grass3.recipes.model.dto.RecipeOverviewDTO;
import cz.gattserver.grass3.template.CreateButton;
import cz.gattserver.grass3.template.ModifyButton;
import cz.gattserver.grass3.ui.util.GrassRequest;

public class RecipesPage extends OneColumnPage {

	private static final long serialVersionUID = -950042653154868289L;

	@Autowired
	private RecipesFacade recipesFacade;

	private VerticalLayout menu;
	private VerticalLayout contentLayout;
	private Label nameLabel;
	private Label contentLabel;
	private RecipeDTO choosenRecipe;

	public RecipesPage(GrassRequest request) {
		super(request);
	}

	@Override
	protected void init() {
		super.init();
	}

	private void showDetail(RecipeDTO choosenRecipe) {
		nameLabel.setValue("<h2>" + choosenRecipe.getName() + "</h2>");
		contentLabel.setValue(recipesFacade.eolToBreakline(choosenRecipe.getDescription()));
		contentLayout.setVisible(true);
		this.choosenRecipe = choosenRecipe;
	}

	private void populateMenu() {
		menu.removeAllComponents();
		for (RecipeOverviewDTO to : recipesFacade.getRecipes()) {
			Button b = new Button(to.getName(), new Button.ClickListener() {
				private static final long serialVersionUID = 7451387366203850483L;

				@Override
				public void buttonClick(ClickEvent event) {
					showDetail(recipesFacade.getRecipeById(to.getId()));
				}
			});
			b.setStyleName(Reindeer.BUTTON_LINK);
			menu.addComponent(b);
		}

		CreateButton createButton = new CreateButton() {
			private static final long serialVersionUID = 7163642213966042835L;

			@Override
			public void onClick(ClickEvent event) {
				UI.getCurrent().addWindow(new RecipeWindow() {
					private static final long serialVersionUID = -4863260002363608014L;

					@Override
					protected void onSave(String name, String desc, Long id) {
						id = recipesFacade.saveRecipe(name, desc);
						RecipeDTO to = new RecipeDTO(id, name, desc);
						showDetail(to);
						populateMenu();
					}
				});
			}
		};
		menu.addComponent(createButton);

	}

	@Override
	protected Component createContent() {
		HorizontalLayout layout = new HorizontalLayout();
		layout.setSpacing(true);
		layout.setMargin(true);

		menu = new VerticalLayout();
		menu.setSpacing(true);
		layout.addComponent(menu);

		contentLayout = new VerticalLayout();
		contentLayout.setSpacing(true);
		layout.addComponent(contentLayout);
		contentLayout.setVisible(false);

		nameLabel = new Label();
		nameLabel.setContentMode(ContentMode.HTML);
		nameLabel.setSizeUndefined();
		contentLayout.addComponent(nameLabel);

		contentLabel = new Label();
		contentLabel.setContentMode(ContentMode.HTML);
		contentLabel.setWidth("700px");
		contentLayout.addComponent(contentLabel);

		HorizontalLayout btnLayout = new HorizontalLayout();
		btnLayout.setSpacing(true);
		contentLayout.addComponent(btnLayout);

		btnLayout.addComponent(new ModifyButton() {
			private static final long serialVersionUID = 3738277265620526416L;

			@Override
			public void onClick(ClickEvent event) {
				UI.getCurrent().addWindow(new RecipeWindow(choosenRecipe) {
					private static final long serialVersionUID = 5264621441522056786L;

					@Override
					protected void onSave(String name, String desc, Long id) {
						recipesFacade.saveRecipe(name, desc, id);
						RecipeDTO to = new RecipeDTO(id, name, desc);
						showDetail(to);
						populateMenu();
					}
				});
			}
		});
		// btnLayout.addComponent(new DeleteButton() {
		// private static final long serialVersionUID = 7163642213966042835L;
		//
		// @Override
		// public void onClick(ClickEvent event) {
		// // TOOD
		// }
		// });

		populateMenu();

		return layout;
	}
}
