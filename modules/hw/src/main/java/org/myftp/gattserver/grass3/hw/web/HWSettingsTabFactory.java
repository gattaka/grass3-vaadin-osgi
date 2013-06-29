package org.myftp.gattserver.grass3.hw.web;

import org.myftp.gattserver.grass3.security.Role;
import org.myftp.gattserver.grass3.tabs.factories.template.AbstractSettingsTabFactory;
import org.springframework.stereotype.Component;

@Component("hwSettingsTabFactory")
public class HWSettingsTabFactory extends AbstractSettingsTabFactory {

	public HWSettingsTabFactory() {
		super("Evidence HW", "hw", "hwSettingsTab");
	}

	public boolean isAuthorized() {
		if (getUser() == null)
			return false;
		return getUser().getRoles().contains(Role.ADMIN);
	}
}
