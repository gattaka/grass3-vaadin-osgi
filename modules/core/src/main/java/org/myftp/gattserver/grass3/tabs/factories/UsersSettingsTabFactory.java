package org.myftp.gattserver.grass3.tabs.factories;

import javax.annotation.Resource;

import org.myftp.gattserver.grass3.security.ICoreACL;
import org.myftp.gattserver.grass3.tabs.factories.template.AbstractSettingsTabFactory;
import org.springframework.stereotype.Component;

@Component("usersSettingsTabFactory")
public class UsersSettingsTabFactory extends AbstractSettingsTabFactory {

	@Resource(name = "coreACL")
	private ICoreACL coreACL;

	public UsersSettingsTabFactory() {
		super("Uživatelé", "users", "usersSettingsTab");
	}

	public boolean isAuthorized() {
		return coreACL.canShowUserSettings(getUser());
	}

}
