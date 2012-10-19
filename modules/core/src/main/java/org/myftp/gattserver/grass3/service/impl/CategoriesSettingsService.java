package org.myftp.gattserver.grass3.service.impl;

import java.util.Set;

import org.myftp.gattserver.grass3.security.Role;
import org.myftp.gattserver.grass3.service.ISettingsService;
import org.myftp.gattserver.grass3.windows.CategoriesSettingsWindow;
import org.myftp.gattserver.grass3.windows.template.GrassWindow;

public class CategoriesSettingsService implements ISettingsService {

	public GrassWindow getSettingsWindowNewInstance() {
		return new CategoriesSettingsWindow();
	}

	public Class<? extends GrassWindow> getSettingsWindowClass() {
		return CategoriesSettingsWindow.class;
	}

	public String getSectionCaption() {
		return "Kategorie";
	}

	public boolean isVisibleForRoles(Set<Role> roles) {
		return roles.contains(Role.ADMIN);
	}

}
