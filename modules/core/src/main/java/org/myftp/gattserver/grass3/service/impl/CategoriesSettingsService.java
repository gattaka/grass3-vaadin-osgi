package org.myftp.gattserver.grass3.service.impl;

import java.util.Set;

import org.myftp.gattserver.grass3.security.Role;
import org.myftp.gattserver.grass3.service.ISettingsService;
import org.myftp.gattserver.grass3.windows.CategoriesSettingsWindow;
import org.myftp.gattserver.grass3.windows.template.SettingsWindow;

public class CategoriesSettingsService implements ISettingsService {

	public SettingsWindow getSettingsWindowNewInstance() {
		return new CategoriesSettingsWindow();
	}

	public Class<? extends SettingsWindow> getSettingsWindowClass() {
		return CategoriesSettingsWindow.class;
	}

	public String getSettingsCaption() {
		return "Kategorie";
	}

	public boolean isVisibleForRoles(Set<Role> roles) {
		return roles.contains(Role.ADMIN);
	}

}
