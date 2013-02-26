package org.myftp.gattserver.grass3.windows.err;

import org.myftp.gattserver.grass3.windows.template.ErrorPage;

public class Err500 extends ErrorPage {

	private static final long serialVersionUID = -2679323424889989397L;

	@Override
	protected String getErrorText() {
		return "500 - Došlo k chybě na straně serveru";
	}

}
