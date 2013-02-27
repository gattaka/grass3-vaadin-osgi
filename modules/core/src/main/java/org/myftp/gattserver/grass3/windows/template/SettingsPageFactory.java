package org.myftp.gattserver.grass3.windows.template;

import java.util.Set;

import org.myftp.gattserver.grass3.security.Role;
import org.myftp.gattserver.grass3.util.GrassRequest;
import org.myftp.gattserver.grass3.windows.SettingsPage;

public abstract class SettingsPageFactory extends PageFactory {

	public SettingsPageFactory(String pageName) {
		super("settings/" + pageName);
	}

	@Override
	public GrassPage createPage(GrassRequest request) {
		return createSettingsPage(request);
	}

	public abstract SettingsPage createSettingsPage(GrassRequest request);

	public abstract String getSettingsCaption();

	public abstract boolean isVisibleForRoles(Set<Role> roles);

}
