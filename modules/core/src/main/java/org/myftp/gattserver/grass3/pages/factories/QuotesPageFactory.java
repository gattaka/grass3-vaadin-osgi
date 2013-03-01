package org.myftp.gattserver.grass3.pages.factories;

import org.myftp.gattserver.grass3.pages.factories.template.PageFactory;
import org.springframework.stereotype.Component;

@Component("quotesPageFactory")
public class QuotesPageFactory extends PageFactory {

	public QuotesPageFactory() {
		super("quotes", "quotesPage");
	}

}
