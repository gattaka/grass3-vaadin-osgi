package org.myftp.gattserver.grass3.hw.web;

import org.myftp.gattserver.grass3.pages.factories.template.AbstractPageFactory;
import org.myftp.gattserver.grass3.pages.template.IGrassPage;
import org.myftp.gattserver.grass3.security.Role;
import org.myftp.gattserver.grass3.ui.util.GrassRequest;
import org.springframework.stereotype.Component;

@Component("hwPageFactory")
public class HWPageFactory extends AbstractPageFactory {

	private static final long serialVersionUID = -8008345071231675889L;

	public HWPageFactory() {
		super("hw");
	}

	@Override
	protected boolean isAuthorized() {
		if (getUser() == null)
			return false;
		return getUser().getRoles().contains(Role.ADMIN);
		// return true;
	}

	@Override
	protected IGrassPage createPage(GrassRequest request) {
		return new HWPage(request);
	}
}
