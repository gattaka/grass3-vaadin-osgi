package cz.gattserver.grass3.monitor.web;

import org.springframework.stereotype.Component;

import cz.gattserver.grass3.ui.pages.factories.template.AbstractPageFactory;
import cz.gattserver.grass3.ui.pages.template.GrassPage;

@Component("monitorPageFactory")
public class MonitorPageFactory extends AbstractPageFactory {

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
	protected GrassPage createPage() {
		return new MonitorPage();
	}
}
