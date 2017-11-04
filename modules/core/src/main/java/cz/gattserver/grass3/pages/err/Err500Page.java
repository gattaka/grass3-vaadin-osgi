package cz.gattserver.grass3.pages.err;

import com.vaadin.server.Resource;
import com.vaadin.server.ThemeResource;

import cz.gattserver.grass3.pages.template.ErrorPage;
import cz.gattserver.grass3.ui.util.GrassRequest;

public class Err500Page extends ErrorPage {

	public Err500Page(GrassRequest request) {
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
