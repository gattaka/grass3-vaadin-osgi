package org.myftp.gattserver.grass3.pages.factories;

import org.myftp.gattserver.grass3.pages.factories.template.AbstractPageFactory;
import org.springframework.stereotype.Component;

@Component(value = "noServicePageFactory")
public class NoServicePageFactory extends AbstractPageFactory {

	public NoServicePageFactory() {
		super("noservice", "noServicePage");
	}

}
