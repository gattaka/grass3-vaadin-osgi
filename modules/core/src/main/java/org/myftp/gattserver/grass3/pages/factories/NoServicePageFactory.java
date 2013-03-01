package org.myftp.gattserver.grass3.pages.factories;

import org.myftp.gattserver.grass3.pages.factories.template.PageFactory;
import org.springframework.stereotype.Component;

@Component(value = "noServicePageFactory")
public class NoServicePageFactory extends PageFactory {

	public NoServicePageFactory() {
		super("noservice", "noServicePage");
	}

}
