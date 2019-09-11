package cz.gattserver.grass3.ui.pages.err;

import cz.gattserver.grass3.server.GrassRequest;
import cz.gattserver.grass3.ui.pages.err.template.ErrorPage;

public class Error404Page extends ErrorPage {

	public Error404Page(GrassRequest request) {
		super(request);
	}

	@Override
	protected String getErrorText() {
		return "404 - Hledaný obsah neexistuje";
	}

	@Override
	protected String getErrorImage() {
		return "img/404.png";
	}

}
