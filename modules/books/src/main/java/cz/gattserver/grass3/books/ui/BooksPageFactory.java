package cz.gattserver.grass3.books.ui;

import org.springframework.stereotype.Component;

import cz.gattserver.grass3.ui.pages.factories.template.AbstractPageFactory;

@Component("booksPageFactory")
public class BooksPageFactory extends AbstractPageFactory {

	public BooksPageFactory() {
		super("books");
	}
}
