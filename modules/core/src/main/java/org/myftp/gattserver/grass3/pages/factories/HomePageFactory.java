package org.myftp.gattserver.grass3.pages.factories;

import org.myftp.gattserver.grass3.pages.factories.template.AbstractPageFactory;
import org.springframework.stereotype.Component;

@Component(value = "homePageFactory")
public class HomePageFactory extends AbstractPageFactory {

	public HomePageFactory() {
		super("home", "homepage");
	}

}
