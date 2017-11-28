package cz.gattserver.grass3.ui.pages.err;

import com.vaadin.server.Resource;
import com.vaadin.server.ThemeResource;

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
	protected Resource getErrorImage() {
		return new ThemeResource("img/404.png");
	}

}