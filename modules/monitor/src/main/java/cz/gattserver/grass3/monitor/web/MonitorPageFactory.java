package cz.gattserver.grass3.monitor.web;

import org.springframework.stereotype.Component;

import cz.gattserver.grass3.pages.factories.template.AbstractPageFactory;
import cz.gattserver.grass3.pages.template.IGrassPage;
import cz.gattserver.grass3.security.Role;
import cz.gattserver.grass3.ui.util.GrassRequest;

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
