package org.myftp.gattserver.grass3.pages.factories;

import java.util.Set;

import org.myftp.gattserver.grass3.pages.factories.template.AbstractSettingsPageFactory;
import org.myftp.gattserver.grass3.security.Role;
import org.springframework.stereotype.Component;

@Component("usersSettingsPageFactory")
public class UsersSettingsPageFactory extends AbstractSettingsPageFactory {

	public UsersSettingsPageFactory() {
		super("users", "usersSettingsPage");
	}

	@Override
	public String getSettingsCaption() {
		return "Uživatelé";
	}

	@Override
	public boolean isVisibleForRoles(Set<Role> roles) {
		return roles.contains(Role.ADMIN);
	}
	
}
