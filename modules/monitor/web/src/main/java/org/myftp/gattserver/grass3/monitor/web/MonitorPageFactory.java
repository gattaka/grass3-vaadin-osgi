package org.myftp.gattserver.grass3.monitor.web;

import org.myftp.gattserver.grass3.pages.factories.template.AbstractPageFactory;
import org.myftp.gattserver.grass3.pages.template.IGrassPage;
import org.myftp.gattserver.grass3.security.Role;
import org.myftp.gattserver.grass3.ui.util.GrassRequest;
import org.springframework.stereotype.Component;

@Component("monitorPageFactory")
public class MonitorPageFactory extends AbstractPageFactory {

	private static final long serialVersionUID = 8984837128014801897L;

	public MonitorPageFactory() {
		super("system-monitor");
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
		return new MonitorPage(request);
	}
}
