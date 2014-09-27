package cz.gattserver.grass3.tabs.factories;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import cz.gattserver.grass3.security.ICoreACL;
import cz.gattserver.grass3.tabs.UsersSettingsTab;
import cz.gattserver.grass3.tabs.factories.template.AbstractSettingsTabFactory;
import cz.gattserver.grass3.tabs.template.ISettingsTab;
import cz.gattserver.grass3.ui.util.GrassRequest;

@Component("usersSettingsTabFactory")
public class UsersSettingsTabFactory extends AbstractSettingsTabFactory {

	@Resource(name = "coreACL")
	private ICoreACL coreACL;

	public UsersSettingsTabFactory() {
		super("Uživatelé", "users");
	}

	public boolean isAuthorized() {
		return coreACL.canShowUserSettings(getUser());
	}

	@Override
	protected ISettingsTab createTab(GrassRequest request) {
		return new UsersSettingsTab(request);
	}

}
