package org.myftp.gattserver.grass3.windows.err;

import org.myftp.gattserver.grass3.windows.template.ErrorPage;

public class Err404 extends ErrorPage {

	private static final long serialVersionUID = 3728073040878360420L;

	@Override
	protected String getErrorText() {
		return "404 - Hledaný obsah neexistuje";
	}

}
