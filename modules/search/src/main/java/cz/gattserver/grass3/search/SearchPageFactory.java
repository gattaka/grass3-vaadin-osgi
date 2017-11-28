package cz.gattserver.grass3.search;

import org.springframework.stereotype.Component;

import cz.gattserver.grass3.server.GrassRequest;
import cz.gattserver.grass3.ui.pages.factories.template.AbstractPageFactory;
import cz.gattserver.grass3.ui.pages.template.GrassPage;

@Component("searchPageFactory")
public class SearchPageFactory extends AbstractPageFactory {

	public SearchPageFactory() {
		super("search");
	}

	@Override
	protected boolean isAuthorized() {
		return true;
	}

	@Override
	protected GrassPage createPage(GrassRequest request) {
		return new SearchPage(request);
	}

}
