package org.myftp.gattserver.grass3.articles.config;

import java.util.Set;

import org.myftp.gattserver.grass3.security.Role;
import org.myftp.gattserver.grass3.service.ISettingsService;
import org.myftp.gattserver.grass3.windows.template.SettingsWindow;


public class Settings implements ISettingsService {

	public SettingsWindow getSettingsWindowNewInstance() {
		// TODO Auto-generated method stub
		return null;
	}

	public Class<? extends SettingsWindow> getSettingsWindowClass() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getSettingsCaption() {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean isVisibleForRoles(Set<Role> roles) {
		// TODO Auto-generated method stub
		return false;
	}



}
