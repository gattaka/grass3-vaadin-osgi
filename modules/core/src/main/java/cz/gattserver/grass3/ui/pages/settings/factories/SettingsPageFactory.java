package cz.gattserver.grass3.ui.pages.settings.factories;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cz.gattserver.grass3.services.CoreACLService;
import cz.gattserver.grass3.ui.pages.factories.template.AbstractPageFactory;
import cz.gattserver.grass3.ui.pages.settings.SettingsPage;
import cz.gattserver.grass3.ui.pages.template.GrassPage;

@Component("settingsPageFactory")
public class SettingsPageFactory extends AbstractPageFactory {

	@Autowired
	private CoreACLService coreACL;

	public SettingsPageFactory() {
		super("settings");
	}

	@Override
	protected boolean isAuthorized() {
		return coreACL.canShowSettings(getUser());
	}

	@Override
	protected GrassPage createPage() {
		return new SettingsPage();
	}
}
