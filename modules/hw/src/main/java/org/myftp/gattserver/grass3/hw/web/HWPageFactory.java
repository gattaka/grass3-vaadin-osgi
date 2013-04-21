package org.myftp.gattserver.grass3.hw.web;

import org.myftp.gattserver.grass3.pages.factories.template.AbstractPageFactory;
import org.myftp.gattserver.grass3.security.Role;
import org.springframework.stereotype.Component;

@Component("hwPageFactory")
public class HWPageFactory extends AbstractPageFactory {

	public HWPageFactory() {
		super("hw", "hwPage");
	}

	@Override
	protected boolean isAuthorized() {
		// if (getUser() == null)
		// return false;
		// return getUser().getRoles().contains(Role.ADMIN);
		return true;
	}

}
