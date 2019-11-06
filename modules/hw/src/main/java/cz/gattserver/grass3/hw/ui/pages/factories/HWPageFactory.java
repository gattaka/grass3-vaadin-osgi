package cz.gattserver.grass3.hw.ui.pages.factories;

import org.springframework.stereotype.Component;

import cz.gattserver.grass3.ui.pages.factories.template.AbstractPageFactory;

@Component("hwPageFactory")
public class HWPageFactory extends AbstractPageFactory {

	public HWPageFactory() {
		super("hw");
	}

}
