package org.myftp.gattserver.grass3.tabs.factories;

import java.util.Set;

import org.myftp.gattserver.grass3.pages.factories.template.SettingsTabFactory;
import org.myftp.gattserver.grass3.security.Role;
import org.springframework.stereotype.Component;

@Component("usersSettingsTabFactory")
public class UsersSettingsTabFactory extends SettingsTabFactory {

	public UsersSettingsTabFactory() {
		super("Uživatelé", "users", "usersSettingsTab");
	}

	@Override
	public boolean isVisibleForRoles(Set<Role> roles) {
		return roles.contains(Role.ADMIN);
	}

}
