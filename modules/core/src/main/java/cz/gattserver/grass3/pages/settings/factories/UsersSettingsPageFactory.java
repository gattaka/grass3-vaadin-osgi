package cz.gattserver.grass3.pages.settings.factories;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cz.gattserver.grass3.pages.settings.ModuleSettingsPage;
import cz.gattserver.grass3.pages.settings.UsersSettingsPage;
import cz.gattserver.grass3.security.CoreACL;
import cz.gattserver.grass3.server.GrassRequest;

@Component("usersSettingsPageFactory")
public class UsersSettingsPageFactory extends AbstractModuleSettingsPageFactory {

	@Autowired
	private CoreACL coreACL;

	public UsersSettingsPageFactory() {
		super("Uživatelé", "users");
	}

	public boolean isAuthorized() {
		return coreACL.canShowUserSettings(getUser());
	}

	@Override
	protected ModuleSettingsPage createPage(GrassRequest request) {
		return new UsersSettingsPage(request);
	}

}
