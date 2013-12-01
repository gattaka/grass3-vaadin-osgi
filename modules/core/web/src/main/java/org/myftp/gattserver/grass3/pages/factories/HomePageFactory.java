package org.myftp.gattserver.grass3.pages.factories;

import org.myftp.gattserver.grass3.pages.factories.template.AbstractPageFactory;
import org.springframework.stereotype.Component;

@Component(value = "homePageFactory")
public class HomePageFactory extends AbstractPageFactory {
	private static final long serialVersionUID = 1836925840118204442L;

	public HomePageFactory() {
		super("home", "homepage");
	}

	@Override
	protected boolean isAuthorized() {
		return true;
	}

}
