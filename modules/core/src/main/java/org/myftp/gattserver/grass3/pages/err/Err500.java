package org.myftp.gattserver.grass3.pages.err;

import org.myftp.gattserver.grass3.pages.template.ErrorPage;
import org.myftp.gattserver.grass3.util.GrassRequest;

public class Err500 extends ErrorPage {

	public Err500(GrassRequest request) {
		super(request);
	}

	private static final long serialVersionUID = -2679323424889989397L;

	@Override
	protected String getErrorText() {
		return "500 - Došlo k chybě na straně serveru";
	}

}
