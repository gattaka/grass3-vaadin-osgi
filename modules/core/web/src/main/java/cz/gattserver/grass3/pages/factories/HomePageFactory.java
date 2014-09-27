package cz.gattserver.grass3.pages.factories;

import org.springframework.stereotype.Component;

import cz.gattserver.grass3.pages.HomePage;
import cz.gattserver.grass3.pages.factories.template.AbstractPageFactory;
import cz.gattserver.grass3.pages.template.IGrassPage;
import cz.gattserver.grass3.ui.util.GrassRequest;

@Component(value = "homePageFactory")
public class HomePageFactory extends AbstractPageFactory {
	private static final long serialVersionUID = 1836925840118204442L;

	public HomePageFactory() {
		super("home");
	}

	@Override
	protected boolean isAuthorized() {
		return true;
	}

	@Override
	protected IGrassPage createPage(GrassRequest request) {
		return new HomePage(request);
	}

}
