package org.myftp.gattserver.grass3.service.impl;

import java.util.Set;

import org.myftp.gattserver.grass3.security.Role;
import org.myftp.gattserver.grass3.service.ISettingsService;
import org.myftp.gattserver.grass3.windows.ApplicationSettingsWindow;
import org.myftp.gattserver.grass3.windows.template.GrassWindow;

public class ApplicationSettingsService implements ISettingsService {

	public GrassWindow getSettingsWindowNewInstance() {
		return new ApplicationSettingsWindow();
	}

	public Class<? extends GrassWindow> getSettingsWindowClass() {
		return ApplicationSettingsWindow.class;
	}

	public String getSectionCaption() {
		return "Aplikace";
	}

	public boolean isVisibleForRoles(Set<Role> roles) {
		return roles.contains(Role.ADMIN);
	}

}
