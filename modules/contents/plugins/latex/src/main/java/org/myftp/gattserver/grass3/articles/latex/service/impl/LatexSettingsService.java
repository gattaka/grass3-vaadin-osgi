package org.myftp.gattserver.grass3.articles.latex.service.impl;

import java.util.Set;

import org.myftp.gattserver.grass3.articles.latex.web.LatexSettingsWindow;
import org.myftp.gattserver.grass3.pages.template.SettingsWindow;
import org.myftp.gattserver.grass3.security.Role;
import org.myftp.gattserver.grass3.service.ISettingsService;

public class LatexSettingsService implements ISettingsService {

	public SettingsWindow getSettingsWindowNewInstance() {
		return new LatexSettingsWindow();
	}

	public Class<? extends SettingsWindow> getSettingsWindowClass() {
		return LatexSettingsWindow.class;
	}

	public String getSettingsCaption() {
		return "LaTeX";
	}

	public boolean isVisibleForRoles(Set<Role> roles) {
		return roles.contains(Role.ADMIN);
	}

}
