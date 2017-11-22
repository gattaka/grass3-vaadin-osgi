package cz.gattserver.grass3.monitor.tabs.factories;

import org.springframework.stereotype.Component;

import cz.gattserver.grass3.monitor.tabs.MonitorSettingsPage;
import cz.gattserver.grass3.server.GrassRequest;
import cz.gattserver.grass3.ui.pages.settings.factories.AbstractModuleSettingsPageFactory;
import cz.gattserver.grass3.ui.pages.template.GrassPage;

@Component
public class MonitorSettingsPageFactory extends AbstractModuleSettingsPageFactory {

	public MonitorSettingsPageFactory() {
		super("System monitor", "system-monitor");
	}

	public boolean isAuthorized() {
		return getUser().isAdmin();
	}

	@Override
	protected GrassPage createPage(GrassRequest request) {
		return new MonitorSettingsPage(request);
	}
}
