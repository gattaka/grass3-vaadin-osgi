package cz.gattserver.grass3.ui.pages.factories;

import org.springframework.stereotype.Component;

import cz.gattserver.grass3.ui.pages.factories.template.AbstractPageFactory;

@Component(value = "homePageFactory")
public class HomePageFactory extends AbstractPageFactory {

	public HomePageFactory() {
		super("home");
	}

}
