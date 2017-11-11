package cz.gattserver.grass3.pages.settings.factories;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cz.gattserver.grass3.pages.settings.ApplicationSettingsPage;
import cz.gattserver.grass3.pages.settings.ModuleSettingsPage;
import cz.gattserver.grass3.security.CoreACL;
import cz.gattserver.grass3.server.GrassRequest;

@Component("applicationSettingsPageFactory")
public class ApplicationSettingsPageFactory extends AbstractModuleSettingsPageFactory {

	@Autowired
	private CoreACL coreACL;

	public ApplicationSettingsPageFactory() {
		super("Aplikace", "app");
	}

	public boolean isAuthorized() {
		return coreACL.canShowApplicationSettings(getUser());
	}

	@Override
	protected ModuleSettingsPage createPage(GrassRequest request) {
		return new ApplicationSettingsPage(request);
	}
}
