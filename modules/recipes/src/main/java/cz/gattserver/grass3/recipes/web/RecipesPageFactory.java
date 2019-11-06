package cz.gattserver.grass3.recipes.web;

import org.springframework.stereotype.Component;

import cz.gattserver.grass3.ui.pages.factories.template.AbstractPageFactory;

@Component("recipesPageFactory")
public class RecipesPageFactory extends AbstractPageFactory {

	public RecipesPageFactory() {
		super("recipes");
	}
}
