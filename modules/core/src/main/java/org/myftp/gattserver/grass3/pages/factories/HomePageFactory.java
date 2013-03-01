package org.myftp.gattserver.grass3.pages.factories;

import org.myftp.gattserver.grass3.pages.factories.template.PageFactory;
import org.springframework.stereotype.Component;

@Component(value = "homePageFactory")
public class HomePageFactory extends PageFactory {

	public HomePageFactory() {
		super("home", "homepage");
	}

}
