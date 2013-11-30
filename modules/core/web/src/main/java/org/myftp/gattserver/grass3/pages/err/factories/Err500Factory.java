package org.myftp.gattserver.grass3.pages.err.factories;

import org.myftp.gattserver.grass3.pages.factories.template.AbstractPageFactory;
import org.springframework.stereotype.Component;

@Component("err500Factory")
public class Err500Factory extends AbstractPageFactory {

	public Err500Factory() {
		super("err500", "err500");
	}

	@Override
	protected boolean isAuthorized() {
		return true;
	}
}
