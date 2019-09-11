package cz.gattserver.grass3.ui.pages.err;

import cz.gattserver.grass3.ui.pages.err.template.ErrorPage;

public class Error403Page extends ErrorPage {

	private static final long serialVersionUID = 206042005308031410L;

	@Override
	protected String getErrorText() {
		return "403 - Nemáte oprávnění k provedení této operace";
	}

	@Override
	protected String getErrorImage() {
		return "img/403.png";
	}

}
