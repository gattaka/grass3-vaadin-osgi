package cz.gattserver.grass3.ui.pages.factories;

import org.springframework.stereotype.Component;

import cz.gattserver.grass3.ui.pages.QuotesPage;
import cz.gattserver.grass3.ui.pages.factories.template.AbstractPageFactory;
import cz.gattserver.grass3.ui.pages.template.GrassPage;

@Component("quotesPageFactory")
public class QuotesPageFactory extends AbstractPageFactory {

	public QuotesPageFactory() {
		super("quotes");
	}

	@Override
	protected boolean isAuthorized() {
		return true;
	}

	@Override
	protected GrassPage createPage() {
		return new QuotesPage();
	}
}
