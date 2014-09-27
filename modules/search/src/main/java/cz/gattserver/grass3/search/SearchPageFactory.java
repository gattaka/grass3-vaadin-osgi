package cz.gattserver.grass3.search;

import org.springframework.stereotype.Component;

import cz.gattserver.grass3.pages.factories.template.AbstractPageFactory;
import cz.gattserver.grass3.pages.template.IGrassPage;
import cz.gattserver.grass3.ui.util.GrassRequest;

@Component("searchPageFactory")
public class SearchPageFactory extends AbstractPageFactory {

	private static final long serialVersionUID = 3240022274263676476L;

	public SearchPageFactory() {
		super("search");
	}

	@Override
	protected boolean isAuthorized() {
		return true;
	}

	@Override
	protected IGrassPage createPage(GrassRequest request) {
		return new SearchPage(request);
	}

}
