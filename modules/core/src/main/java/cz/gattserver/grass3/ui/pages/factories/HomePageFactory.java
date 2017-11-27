package cz.gattserver.grass3.ui.pages.factories;

import org.springframework.stereotype.Component;

import cz.gattserver.grass3.server.GrassRequest;
import cz.gattserver.grass3.ui.pages.HomePage;
import cz.gattserver.grass3.ui.pages.factories.template.AbstractPageFactory;
import cz.gattserver.grass3.ui.pages.template.GrassPage;

@Component(value = "homePageFactory")
public class HomePageFactory extends AbstractPageFactory {

	public HomePageFactory() {
		super("home");
	}

	@Override
	protected boolean isAuthorized() {
		return true;
	}

	@Override
	protected GrassPage createPage(GrassRequest request) {
		return new HomePage(request);
	}

}
