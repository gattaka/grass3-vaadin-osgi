package org.myftp.gattserver.grass3.pages.factories;

import java.util.Set;

import org.myftp.gattserver.grass3.pages.factories.template.AbstractSettingsPageFactory;
import org.myftp.gattserver.grass3.security.Role;
import org.springframework.stereotype.Component;

@Component("applicationSettingsPageFactory")
public class ApplicationSettingsPageFactory extends AbstractSettingsPageFactory {

	public ApplicationSettingsPageFactory() {
		super("application", "applicationSettingsPage");
	}

	@Override
	public String getSettingsCaption() {
		return "Aplikace";
	}

	@Override
	public boolean isVisibleForRoles(Set<Role> roles) {
		return roles.contains(Role.ADMIN);
	}
	
}
