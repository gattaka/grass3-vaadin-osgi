package org.myftp.gattserver.grass3.fm.web.factories;

import org.myftp.gattserver.grass3.pages.factories.template.AbstractPageFactory;
import org.myftp.gattserver.grass3.security.Role;
import org.springframework.stereotype.Component;

@Component("fmPageFactory")
public class FMPageFactory extends AbstractPageFactory {

	public FMPageFactory() {
		super("fm", "fmPage");
	}

	@Override
	protected boolean isAuthorized() {
		if (getUser() == null)
			return false;
		return getUser().getRoles().contains(Role.ADMIN)
				|| getUser().getRoles().contains(Role.FRIEND);
//		return true;
	}

}
