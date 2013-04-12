package org.myftp.gattserver.grass3.pages.err;

import org.myftp.gattserver.grass3.pages.template.ErrorPage;
import org.myftp.gattserver.grass3.util.GrassRequest;
import org.springframework.context.annotation.Scope;

@org.springframework.stereotype.Component("err404")
@Scope("prototype")
public class Err404 extends ErrorPage {

	public Err404(GrassRequest request) {
		super(request);
	}

	private static final long serialVersionUID = 3728073040878360420L;

	@Override
	protected String getErrorText() {
		return "404 - Hledaný obsah neexistuje";
	}

}
