package cz.gattserver.grass3.monitor.web;

import org.springframework.stereotype.Component;

import cz.gattserver.grass3.pages.factories.template.AbstractPageFactory;
import cz.gattserver.grass3.pages.template.GrassPage;
import cz.gattserver.grass3.server.GrassRequest;

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
		return getUser().isAdmin();
	}

	@Override
	protected GrassPage createPage(GrassRequest request) {
		return new MonitorPage(request);
	}
}
