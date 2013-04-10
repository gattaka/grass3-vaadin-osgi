package org.myftp.gattserver.grass3.pages.factories;

import org.myftp.gattserver.grass3.pages.factories.template.AbstractPageFactory;
import org.springframework.stereotype.Component;

@Component("quotesPageFactory")
public class QuotesPageFactory extends AbstractPageFactory {

	public QuotesPageFactory() {
		super("quotes", "quotesPage");
	}

}
