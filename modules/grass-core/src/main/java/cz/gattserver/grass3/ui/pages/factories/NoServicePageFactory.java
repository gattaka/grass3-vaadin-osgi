package cz.gattserver.grass3.ui.pages.factories;

import org.springframework.stereotype.Component;

import cz.gattserver.grass3.ui.pages.factories.template.AbstractPageFactory;

@Component(value = "noServicePageFactory")
public class NoServicePageFactory extends AbstractPageFactory {

	public NoServicePageFactory() {
		super("noservice");
	}

}
