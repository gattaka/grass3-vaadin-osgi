package org.myftp.gattserver.grass3.hw.web;

import org.myftp.gattserver.grass3.security.Role;
import org.myftp.gattserver.grass3.tabs.factories.template.AbstractSettingsTabFactory;
import org.myftp.gattserver.grass3.tabs.template.ISettingsTab;
import org.myftp.gattserver.grass3.ui.util.GrassRequest;
import org.springframework.stereotype.Component;

@Component("hwSettingsTabFactory")
public class HWSettingsTabFactory extends AbstractSettingsTabFactory {

	public HWSettingsTabFactory() {
		super("Evidence HW", "hw");
	}

	public boolean isAuthorized() {
		if (getUser() == null)
			return false;
		return getUser().getRoles().contains(Role.ADMIN);
	}

	@Override
	protected ISettingsTab createTab(GrassRequest request) {
		return new HWSettingsTab(request);
	}
}
