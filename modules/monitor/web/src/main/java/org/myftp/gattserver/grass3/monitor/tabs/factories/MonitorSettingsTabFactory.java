package org.myftp.gattserver.grass3.monitor.tabs.factories;

import org.myftp.gattserver.grass3.monitor.tabs.MonitorSettingsTab;
import org.myftp.gattserver.grass3.security.Role;
import org.myftp.gattserver.grass3.tabs.factories.template.AbstractSettingsTabFactory;
import org.myftp.gattserver.grass3.tabs.template.ISettingsTab;
import org.myftp.gattserver.grass3.ui.util.GrassRequest;
import org.springframework.stereotype.Component;

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
