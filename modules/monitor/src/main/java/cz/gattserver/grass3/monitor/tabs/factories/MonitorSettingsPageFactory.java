package cz.gattserver.grass3.monitor.tabs.factories;

import org.springframework.stereotype.Component;

import cz.gattserver.grass3.monitor.tabs.MonitorSettingsPage;
import cz.gattserver.grass3.pages.template.GrassPage;
import cz.gattserver.grass3.tabs.factories.template.AbstractModuleSettingsPageFactory;
import cz.gattserver.grass3.ui.util.GrassRequest;

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
