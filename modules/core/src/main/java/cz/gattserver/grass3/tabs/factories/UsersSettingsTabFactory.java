package cz.gattserver.grass3.tabs.factories;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cz.gattserver.grass3.security.CoreACL;
import cz.gattserver.grass3.tabs.UsersSettingsPage;
import cz.gattserver.grass3.tabs.factories.template.AbstractModuleSettingsPageFactory;
import cz.gattserver.grass3.tabs.template.ModuleSettingsPage;
import cz.gattserver.grass3.ui.util.GrassRequest;

@Component("usersSettingsTabFactory")
public class UsersSettingsTabFactory extends AbstractModuleSettingsPageFactory {

	@Autowired
	private CoreACL coreACL;

	public UsersSettingsTabFactory() {
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
