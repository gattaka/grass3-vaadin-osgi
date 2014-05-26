package org.myftp.gattserver.grass3.pages.factories;

import org.myftp.gattserver.grass3.pages.HomePage;
import org.myftp.gattserver.grass3.pages.factories.template.AbstractPageFactory;
import org.myftp.gattserver.grass3.pages.template.IGrassPage;
import org.myftp.gattserver.grass3.ui.util.GrassRequest;
import org.springframework.stereotype.Component;

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
