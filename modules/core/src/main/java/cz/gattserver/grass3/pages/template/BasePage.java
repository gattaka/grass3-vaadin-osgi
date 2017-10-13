package cz.gattserver.grass3.pages.template;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.ui.CustomLayout;
import com.vaadin.ui.Link;

import cz.gattserver.grass3.facades.QuotesFacade;
import cz.gattserver.grass3.pages.factories.template.PageFactory;
import cz.gattserver.grass3.ui.util.GrassRequest;

public abstract class BasePage extends AbstractGrassPage {

	private static final long serialVersionUID = 502625699429764791L;

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
			showError500();
		}
		return quote;
	}

}