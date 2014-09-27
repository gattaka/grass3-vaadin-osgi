package cz.gattserver.grass3.tabs.factories;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import cz.gattserver.grass3.security.ICoreACL;
import cz.gattserver.grass3.tabs.ApplicationSettingsTab;
import cz.gattserver.grass3.tabs.factories.template.AbstractSettingsTabFactory;
import cz.gattserver.grass3.tabs.template.ISettingsTab;
import cz.gattserver.grass3.ui.util.GrassRequest;

@Component("applicationSettingsTabFactory")
public class ApplicationSettingsTabFactory extends AbstractSettingsTabFactory {

	@Resource(name = "coreACL")
	private ICoreACL coreACL;

	public ApplicationSettingsTabFactory() {
		super("Aplikace", "app");
	}

	public boolean isAuthorized() {
		return coreACL.canShowApplicationSettings(getUser());
	}

	@Override
	protected ISettingsTab createTab(GrassRequest request) {
		return new ApplicationSettingsTab(request);
	}
}
