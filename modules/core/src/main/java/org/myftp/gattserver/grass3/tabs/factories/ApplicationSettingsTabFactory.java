package org.myftp.gattserver.grass3.tabs.factories;

import java.util.Set;

import org.myftp.gattserver.grass3.security.Role;
import org.myftp.gattserver.grass3.tabs.factories.template.AbstractSettingsTabFactory;
import org.springframework.stereotype.Component;

@Component("applicationSettingsTabFactory")
public class ApplicationSettingsTabFactory extends AbstractSettingsTabFactory {

	public ApplicationSettingsTabFactory() {
		super("Aplikace", "app", "applicationSettingsTab");
	}

	@Override
	public boolean isVisibleForRoles(Set<Role> roles) {
		return roles.contains(Role.ADMIN);
	}

}
