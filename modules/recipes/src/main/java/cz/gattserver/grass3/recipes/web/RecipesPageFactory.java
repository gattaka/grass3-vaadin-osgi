package cz.gattserver.grass3.recipes.web;

import org.springframework.stereotype.Component;

import cz.gattserver.grass3.ui.pages.factories.template.AbstractPageFactory;
import cz.gattserver.grass3.ui.pages.template.GrassPage;

@Component("recipesPageFactory")
public class RecipesPageFactory extends AbstractPageFactory {

	public RecipesPageFactory() {
		super("recipes");
	}

	@Override
	protected boolean isAuthorized() {
		return true;
	}

	@Override
	protected GrassPage createPage() {
		return new RecipesPage();
	}
}
