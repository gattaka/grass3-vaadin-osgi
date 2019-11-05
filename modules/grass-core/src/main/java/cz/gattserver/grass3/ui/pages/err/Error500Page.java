package cz.gattserver.grass3.ui.pages.err;

import com.vaadin.flow.router.Route;

import cz.gattserver.grass3.ui.pages.err.template.ErrorPage;

@Route("err500")
public class Error500Page extends ErrorPage {

	private static final long serialVersionUID = 4897703254037309745L;

	@Override
	protected String getErrorText() {
		return "500 - Došlo k chybě na straně serveru";
	}

	@Override
	protected String getErrorImage() {
		return "img/500.png";
	}

}