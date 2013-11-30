package org.myftp.gattserver.grass3.pages.err.factories;

import org.myftp.gattserver.grass3.pages.factories.template.AbstractPageFactory;
import org.springframework.stereotype.Component;

@Component("err403Factory")
public class Err403Factory extends AbstractPageFactory {

	public Err403Factory() {
		super("err403", "err403");
	}

	@Override
	protected boolean isAuthorized() {
		return true;
	}
}
