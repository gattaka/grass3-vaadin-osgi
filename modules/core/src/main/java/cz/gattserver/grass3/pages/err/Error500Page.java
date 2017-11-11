package cz.gattserver.grass3.pages.err;

import com.vaadin.server.Resource;
import com.vaadin.server.ThemeResource;

import cz.gattserver.grass3.pages.err.template.ErrorPage;
import cz.gattserver.grass3.server.GrassRequest;

public class Error500Page extends ErrorPage {

	public Error500Page(GrassRequest request) {
		super(request);
	}

	@Override
	protected String getErrorText() {
		return "500 - Došlo k chybě na straně serveru";
	}

	@Override
	protected Resource getErrorImage() {
		return new ThemeResource("img/500.png");
	}

}
