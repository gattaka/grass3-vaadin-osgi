package org.myftp.gattserver.grass3.pages.factories;

import org.myftp.gattserver.grass3.pages.QuotesPage;
import org.myftp.gattserver.grass3.pages.factories.template.AbstractPageFactory;
import org.myftp.gattserver.grass3.pages.template.IGrassPage;
import org.myftp.gattserver.grass3.ui.util.GrassRequest;
import org.springframework.stereotype.Component;

@Component("quotesPageFactory")
public class QuotesPageFactory extends AbstractPageFactory {

	private static final long serialVersionUID = 130456003400328236L;

	public QuotesPageFactory() {
		super("quotes");
	}

	@Override
	protected boolean isAuthorized() {
		return true;
	}

	@Override
	protected IGrassPage createPage(GrassRequest request) {
		return new QuotesPage(request);
	}
}
