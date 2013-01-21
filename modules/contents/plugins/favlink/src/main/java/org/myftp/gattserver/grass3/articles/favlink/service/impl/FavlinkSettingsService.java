package org.myftp.gattserver.grass3.articles.favlink.service.impl;

import java.util.Set;

import org.myftp.gattserver.grass3.articles.favlink.web.FavlinkSettingsWindow;
import org.myftp.gattserver.grass3.security.Role;
import org.myftp.gattserver.grass3.service.ISettingsService;
import org.myftp.gattserver.grass3.windows.template.SettingsWindow;

public class FavlinkSettingsService implements ISettingsService {

	public SettingsWindow getSettingsWindowNewInstance() {
		return new FavlinkSettingsWindow();
	}

	public Class<? extends SettingsWindow> getSettingsWindowClass() {
		return FavlinkSettingsWindow.class;
	}

	public String getSettingsCaption() {
		return "Favlink";
	}

	public boolean isVisibleForRoles(Set<Role> roles) {
		return roles.contains(Role.ADMIN);
	}

}
