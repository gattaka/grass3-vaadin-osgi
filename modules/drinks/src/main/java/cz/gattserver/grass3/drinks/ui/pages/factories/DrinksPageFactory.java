package cz.gattserver.grass3.drinks.ui.pages.factories;

import org.springframework.stereotype.Component;

import cz.gattserver.grass3.drinks.ui.pages.DrinksPage;
import cz.gattserver.grass3.server.GrassRequest;
import cz.gattserver.grass3.ui.pages.factories.template.AbstractPageFactory;
import cz.gattserver.grass3.ui.pages.template.GrassPage;

@Component("drinksPageFactory")
public class DrinksPageFactory extends AbstractPageFactory {

	public DrinksPageFactory() {
		super("drinks");
	}

	@Override
	protected boolean isAuthorized() {
		return true;
	}

	@Override
	protected GrassPage createPage(GrassRequest request) {
		return new DrinksPage(request);
	}
}
