package cz.gattserver.grass3.monitor.tabs.factories;

import org.springframework.stereotype.Component;

import cz.gattserver.grass3.monitor.tabs.MonitorSettingsTab;
import cz.gattserver.grass3.security.Role;
import cz.gattserver.grass3.tabs.factories.template.AbstractSettingsTabFactory;
import cz.gattserver.grass3.tabs.template.ISettingsTab;
import cz.gattserver.grass3.ui.util.GrassRequest;

@Component("monitorSettingsTabFactory")
public class MonitorSettingsTabFactory extends AbstractSettingsTabFactory {

	public MonitorSettingsTabFactory() {
		super("System monitor", "system-monitor");
	}

	public boolean isAuthorized() {
		return getUser().getRoles().contains(Role.ADMIN);
	}

	@Override
	protected ISettingsTab createTab(GrassRequest request) {
		return new MonitorSettingsTab(request);
	}
}
