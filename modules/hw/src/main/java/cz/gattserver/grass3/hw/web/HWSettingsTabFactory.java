package cz.gattserver.grass3.hw.web;

import org.springframework.stereotype.Component;

import cz.gattserver.grass3.tabs.factories.template.AbstractSettingsTabFactory;
import cz.gattserver.grass3.tabs.template.SettingsTab;
import cz.gattserver.grass3.ui.util.GrassRequest;

@Component("hwSettingsTabFactory")
public class HWSettingsTabFactory extends AbstractSettingsTabFactory {

	public HWSettingsTabFactory() {
		super("Evidence HW", "hw");
	}

	public boolean isAuthorized() {
		if (getUser() == null)
			return false;
		return getUser().isAdmin();
	}

	@Override
	protected SettingsTab createTab(GrassRequest request) {
		return new HWSettingsTab(request);
	}
}
