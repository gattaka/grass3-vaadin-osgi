package cz.gattserver.grass3.ui.pages.err;

import cz.gattserver.grass3.ui.pages.err.template.ErrorPage;

public class Error404Page extends ErrorPage {

	private static final long serialVersionUID = 8346513920500065101L;

	@Override
	protected String getErrorText() {
		return "404 - Hledaný obsah neexistuje";
	}

	@Override
	protected String getErrorImage() {
		return "img/404.png";
	}

}
