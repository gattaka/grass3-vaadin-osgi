package cz.gattserver.grass3.pages.template;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.ui.CustomLayout;
import com.vaadin.ui.Link;

import cz.gattserver.grass3.facades.QuotesFacade;
import cz.gattserver.grass3.pages.factories.template.PageFactory;
import cz.gattserver.grass3.server.GrassRequest;
import cz.gattserver.grass3.ui.util.UIUtils;

public abstract class BasePage extends MenuPage {

	@Autowired
	protected QuotesFacade quotesFacade;

	@Resource(name = "quotesPageFactory")
	protected PageFactory quotesPageFactory;

	public BasePage(GrassRequest request) {
		super(request);
	}

	@Override
	protected void createQuotes(CustomLayout layout) {

		// hlášky
		Link quotes = new Link();
		quotes.setResource(getPageResource(quotesPageFactory));
		quotes.setStyleName("quote");
		quotes.setCaption("\"" + chooseQuote() + "\"");

		layout.addComponent(quotes, "quote");
	}

	private String chooseQuote() {
		String quote = quotesFacade.getRandomQuote();
		if (quote == null) {
			UIUtils.showErrorPage500();
		}
		return quote;
	}

}
