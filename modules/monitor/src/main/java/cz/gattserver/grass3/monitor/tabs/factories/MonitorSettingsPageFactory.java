package cz.gattserver.grass3.monitor.tabs.factories;

import org.springframework.stereotype.Component;

import cz.gattserver.grass3.monitor.tabs.MonitorSettingsPageFragmentFactory;
import cz.gattserver.grass3.ui.pages.settings.AbstractModuleSettingsPageFactory;
import cz.gattserver.grass3.ui.pages.settings.AbstractPageFragmentFactory;

@Component
public class MonitorSettingsPageFactory extends AbstractModuleSettingsPageFactory {

	public MonitorSettingsPageFactory() {
		super("System monitor", "system-monitor");
	}

	public boolean isAuthorized() {
		return getUser().isAdmin();
	}

	@Override
	protected AbstractPageFragmentFactory createPageFragmentFactory() {
		return new MonitorSettingsPageFragmentFactory();
	}
}
