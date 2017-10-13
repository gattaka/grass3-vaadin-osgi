package cz.gattserver.grass3.pages.err;

import com.vaadin.server.Resource;
import com.vaadin.server.ThemeResource;

import cz.gattserver.grass3.pages.template.ErrorPage;
import cz.gattserver.grass3.ui.util.GrassRequest;

public class Err404Page extends ErrorPage {

	public Err404Page(GrassRequest request) {
		super(request);
	}

	private static final long serialVersionUID = 3728073040878360420L;

	@Override
	protected String getErrorText() {
		return "404 - Hledaný obsah neexistuje";
	}
	
	@Override
	protected Resource getErrorImage() {
		return new ThemeResource("img/404.png");
	}

}