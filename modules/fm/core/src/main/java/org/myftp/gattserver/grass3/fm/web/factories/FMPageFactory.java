package org.myftp.gattserver.grass3.fm.web.factories;

import org.myftp.gattserver.grass3.fm.web.FMPage;
import org.myftp.gattserver.grass3.pages.factories.template.AbstractPageFactory;
import org.myftp.gattserver.grass3.pages.template.IGrassPage;
import org.myftp.gattserver.grass3.security.Role;
import org.myftp.gattserver.grass3.ui.util.GrassRequest;
import org.springframework.stereotype.Component;

@Component("fmPageFactory")
public class FMPageFactory extends AbstractPageFactory {

	private static final long serialVersionUID = -2643365441921408821L;

	public FMPageFactory() {
		super("fm");
	}

	@Override
	protected boolean isAuthorized() {
		if (getUser() == null)
			return false;
		return getUser().getRoles().contains(Role.ADMIN) || getUser().getRoles().contains(Role.FRIEND);
		// return true;
	}

	@Override
	protected IGrassPage createPage(GrassRequest request) {
		return new FMPage(request);
	}

}
