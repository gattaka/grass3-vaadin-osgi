package cz.gattserver.grass3.books.ui;

import org.springframework.stereotype.Component;

import cz.gattserver.grass3.server.GrassRequest;
import cz.gattserver.grass3.ui.pages.factories.template.AbstractPageFactory;
import cz.gattserver.grass3.ui.pages.template.GrassPage;

@Component("booksPageFactory")
public class BooksPageFactory extends AbstractPageFactory {

	public BooksPageFactory() {
		super("books");
	}

	@Override
	protected boolean isAuthorized() {
		return true;
	}

	@Override
	protected GrassPage createPage(GrassRequest request) {
		return new BooksPage(request);
	}
}
