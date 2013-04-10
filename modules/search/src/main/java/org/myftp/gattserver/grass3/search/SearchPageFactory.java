package org.myftp.gattserver.grass3.search;

import org.myftp.gattserver.grass3.pages.factories.template.AbstractPageFactory;
import org.springframework.stereotype.Component;

@Component("searchPageFactory")
public class SearchPageFactory extends AbstractPageFactory {

	public SearchPageFactory() {
		super("search", "searchPage");
	}

}
