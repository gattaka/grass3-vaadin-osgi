package org.myftp.gattserver.grass3.pages.err;

import org.myftp.gattserver.grass3.pages.template.ErrorPage;

public class Err404 extends ErrorPage {

	private static final long serialVersionUID = 3728073040878360420L;

	@Override
	protected String getErrorText() {
		return "404 - Hledaný obsah neexistuje";
	}

}
