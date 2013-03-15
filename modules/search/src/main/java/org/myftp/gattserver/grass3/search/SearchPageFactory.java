package org.myftp.gattserver.grass3.search;

import org.myftp.gattserver.grass3.pages.factories.template.PageFactory;
import org.springframework.stereotype.Component;

@Component("searchPageFactory")
public class SearchPageFactory extends PageFactory {

	public SearchPageFactory() {
		super("search", "searchPage");
	}

}
