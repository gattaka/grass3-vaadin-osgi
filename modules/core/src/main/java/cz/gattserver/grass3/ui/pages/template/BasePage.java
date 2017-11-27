package cz.gattserver.grass3.ui.pages.template;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.ui.CustomLayout;
import com.vaadin.ui.Link;

import cz.gattserver.grass3.exception.GrassPageException;
import cz.gattserver.grass3.server.GrassRequest;
import cz.gattserver.grass3.services.QuotesService;
import cz.gattserver.grass3.ui.pages.factories.template.PageFactory;

public abstract class BasePage extends MenuPage {

	@Autowired
	protected QuotesService quotesFacade;

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
		if (quote == null)
			throw new GrassPageException(500);
		return quote;
	}

}
