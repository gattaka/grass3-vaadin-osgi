package org.myftp.gattserver.grass3.pages.factories;

import org.myftp.gattserver.grass3.pages.factories.template.AbstractPageFactory;
import org.springframework.stereotype.Component;

@Component("quotesPageFactory")
public class QuotesPageFactory extends AbstractPageFactory {

	private static final long serialVersionUID = 130456003400328236L;

	public QuotesPageFactory() {
		super("quotes", "quotesPage");
	}

	@Override
	protected boolean isAuthorized() {
		return true;
	}
}
