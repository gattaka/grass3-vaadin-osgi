package cz.gattserver.grass3.pages.err;

import com.vaadin.server.Resource;
import com.vaadin.server.ThemeResource;

import cz.gattserver.grass3.pages.template.ErrorPage;
import cz.gattserver.grass3.ui.util.GrassRequest;

public class Err403Page extends ErrorPage {

	public Err403Page(GrassRequest request) {
		super(request);
	}

	private static final long serialVersionUID = 3728073040878360420L;

	@Override
	protected String getErrorText() {
		return "403 - Nemáte oprávnění k provedení této operace";
	}

	@Override
	protected Resource getErrorImage() {
		return new ThemeResource("img/403.png");
	}

}
