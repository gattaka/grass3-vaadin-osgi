package org.myftp.gattserver.grass3.pages.err.factories;

import org.myftp.gattserver.grass3.pages.factories.template.AbstractPageFactory;
import org.springframework.stereotype.Component;

@Component("err404Factory")
public class Err404Factory extends AbstractPageFactory {

	public Err404Factory() {
		super("err404", "err404");
	}

	@Override
	protected boolean isAuthorized() {
		return true;
	}
}
