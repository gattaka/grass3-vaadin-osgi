package cz.gattserver.grass3.tabs.factories;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cz.gattserver.grass3.security.CoreACL;
import cz.gattserver.grass3.tabs.ApplicationSettingsTab;
import cz.gattserver.grass3.tabs.factories.template.AbstractSettingsTabFactory;
import cz.gattserver.grass3.tabs.template.SettingsTab;
import cz.gattserver.grass3.ui.util.GrassRequest;

@Component("applicationSettingsTabFactory")
public class ApplicationSettingsTabFactory extends AbstractSettingsTabFactory {

	@Autowired
	private CoreACL coreACL;

	public ApplicationSettingsTabFactory() {
		super("Aplikace", "app");
	}

	public boolean isAuthorized() {
		return coreACL.canShowApplicationSettings(getUser());
	}

	@Override
	protected SettingsTab createTab(GrassRequest request) {
		return new ApplicationSettingsTab(request);
	}
}
