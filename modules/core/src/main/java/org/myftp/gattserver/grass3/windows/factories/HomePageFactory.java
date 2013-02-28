package org.myftp.gattserver.grass3.windows.factories;

import org.myftp.gattserver.grass3.util.GrassRequest;
import org.myftp.gattserver.grass3.windows.template.GrassPage;
import org.myftp.gattserver.grass3.windows.template.PageFactory;
import org.springframework.stereotype.Component;

@Component(value = "homePageFactory")
public class HomePageFactory extends PageFactory {

	public HomePageFactory() {
		super("home");
	}

	@Override
	public GrassPage createPage(GrassRequest request) {
		return (GrassPage) applicationContext.getBean("homepage", request);
	}

}
