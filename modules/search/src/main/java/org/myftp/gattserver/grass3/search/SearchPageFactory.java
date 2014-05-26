package org.myftp.gattserver.grass3.search;

import org.myftp.gattserver.grass3.pages.factories.template.AbstractPageFactory;
import org.myftp.gattserver.grass3.pages.template.IGrassPage;
import org.myftp.gattserver.grass3.ui.util.GrassRequest;
import org.springframework.stereotype.Component;

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
