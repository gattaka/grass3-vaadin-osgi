package org.myftp.gattserver.grass3.windows.err;

import org.myftp.gattserver.grass3.windows.template.ErrorWindow;

public class Err500 extends ErrorWindow {

	private static final long serialVersionUID = -2679323424889989397L;

	public static final String NAME = "500";

	public Err500() {
		super(NAME);
	}

	@Override
	protected String getErrorText() {
		return "Došlo k chybě na straně serveru";
	}

}
