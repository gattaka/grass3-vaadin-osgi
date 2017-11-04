package cz.gattserver.grass3.tabs.factories;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cz.gattserver.grass3.security.CoreACL;
import cz.gattserver.grass3.tabs.ApplicationSettingsPage;
import cz.gattserver.grass3.tabs.factories.template.AbstractModuleSettingsPageFactory;
import cz.gattserver.grass3.tabs.template.ModuleSettingsPage;
import cz.gattserver.grass3.ui.util.GrassRequest;

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
