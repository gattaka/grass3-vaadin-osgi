package cz.gattserver.grass3.fm.web.factories;

import org.springframework.stereotype.Component;

import cz.gattserver.grass3.ui.pages.factories.template.AbstractPageFactory;

@Component("fmPageFactory")
public class FMPageFactory extends AbstractPageFactory {

	public FMPageFactory() {
		super("fm");
	}

}
