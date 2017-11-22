package cz.gattserver.grass3.ui.pages.err;

import com.vaadin.server.Resource;
import com.vaadin.server.ThemeResource;

import cz.gattserver.grass3.server.GrassRequest;
import cz.gattserver.grass3.ui.pages.err.template.ErrorPage;

public class Error403Page extends ErrorPage {

	public Error403Page(GrassRequest request) {
		super(request);
	}

	@Override
	protected String getErrorText() {
		return "403 - Nemáte oprávnění k provedení této operace";
	}

	@Override
	protected Resource getErrorImage() {
		return new ThemeResource("img/403.png");
	}

}
