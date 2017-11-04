package cz.gattserver.grass3.pages.err;

import com.vaadin.server.Resource;
import com.vaadin.server.ThemeResource;

import cz.gattserver.grass3.pages.template.ErrorPage;
import cz.gattserver.grass3.ui.util.GrassRequest;

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
