package cz.gattserver.grass3.ui.pages.factories;

import org.springframework.stereotype.Component;

import cz.gattserver.grass3.ui.pages.factories.template.AbstractPageFactory;

@Component("quotesPageFactory")
public class QuotesPageFactory extends AbstractPageFactory {

	public QuotesPageFactory() {
		super("quotes");
	}

}
