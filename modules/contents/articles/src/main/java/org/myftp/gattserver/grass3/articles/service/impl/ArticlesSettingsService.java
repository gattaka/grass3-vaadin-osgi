package org.myftp.gattserver.grass3.articles.service.impl;

import java.util.Set;

import org.myftp.gattserver.grass3.articles.windows.ArticlesSettingsWindow;
import org.myftp.gattserver.grass3.security.Role;
import org.myftp.gattserver.grass3.service.ISettingsService;
import org.myftp.gattserver.grass3.windows.template.SettingsWindow;

public class ArticlesSettingsService implements ISettingsService {

	public SettingsWindow getSettingsWindowNewInstance() {
		return new ArticlesSettingsWindow();
	}

	public Class<? extends SettingsWindow> getSettingsWindowClass() {
		return ArticlesSettingsWindow.class;
	}

	public String getSettingsCaption() {
		return "Články";
	}

	public boolean isVisibleForRoles(Set<Role> roles) {
		return roles.contains(Role.ADMIN);
	}

}
