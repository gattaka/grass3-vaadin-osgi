package cz.gattserver.grass3.ui.pages.err;

import cz.gattserver.grass3.server.GrassRequest;
import cz.gattserver.grass3.ui.pages.err.template.ErrorPage;

public class Error500Page extends ErrorPage {

	public Error500Page(GrassRequest request) {
		super(request);
	}

	@Override
	protected String getErrorText() {
		return "500 - Došlo k chybě na straně serveru";
	}

	@Override
	protected String getErrorImage() {
		return "img/500.png";
	}

}
