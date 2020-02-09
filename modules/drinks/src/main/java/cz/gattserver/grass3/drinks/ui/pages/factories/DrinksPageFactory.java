package cz.gattserver.grass3.drinks.ui.pages.factories;

import org.springframework.stereotype.Component;

import cz.gattserver.grass3.ui.pages.factories.template.AbstractPageFactory;

@Component("drinksPageFactory")
public class DrinksPageFactory extends AbstractPageFactory {

	public DrinksPageFactory() {
		super("drinks");
	}
}
