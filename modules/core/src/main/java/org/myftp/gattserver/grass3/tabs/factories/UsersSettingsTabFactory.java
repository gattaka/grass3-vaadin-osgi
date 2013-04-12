package org.myftp.gattserver.grass3.tabs.factories;

import org.myftp.gattserver.grass3.tabs.factories.template.AbstractSettingsTabFactory;
import org.springframework.stereotype.Component;

@Component("usersSettingsTabFactory")
public class UsersSettingsTabFactory extends AbstractSettingsTabFactory {

	public UsersSettingsTabFactory() {
		super("Uživatelé", "users", "usersSettingsTab");
	}

	@Override
	protected boolean isAuthorized() {
		return getUserACL().canShowUserSettings();
	}

}
