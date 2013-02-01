package org.myftp.gattserver.grass3.windows.err;

import org.myftp.gattserver.grass3.windows.template.ErrorWindow;

public class Err404 extends ErrorWindow {

	private static final long serialVersionUID = 3728073040878360420L;

	public static final String NAME = "404";

	public Err404() {
		super(NAME);
	}

	@Override
	protected String getErrorText() {
		return "Hledaný obsah neexistuje";
	}

}
