package cz.gattserver.grass3.ui.pages.settings.factories;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cz.gattserver.grass3.services.CoreACLService;
import cz.gattserver.grass3.ui.pages.settings.AbstractModuleSettingsPageFactory;
import cz.gattserver.grass3.ui.pages.settings.AbstractPageFragmentFactory;

@Component("applicationSettingsPageFactory")
public class ApplicationSettingsPageFactory extends AbstractModuleSettingsPageFactory {

	@Autowired
	private CoreACLService coreACL;

	public ApplicationSettingsPageFactory() {
		super("Aplikace", "app");
	}

	public boolean isAuthorized() {
		return coreACL.canShowApplicationSettings(getUser());
	}

	@Override
	protected AbstractPageFragmentFactory createPageFragmentFactory() {
		return new ApplicationSettingsPageFragmentFactory();
	}
}
