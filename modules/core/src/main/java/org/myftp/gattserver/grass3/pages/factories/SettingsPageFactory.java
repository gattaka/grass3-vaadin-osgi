package org.myftp.gattserver.grass3.pages.factories;

import org.myftp.gattserver.grass3.pages.factories.template.AbstractPageFactory;
import org.springframework.stereotype.Component;

@Component("settingsPageFactory")
public class SettingsPageFactory extends AbstractPageFactory {

	public SettingsPageFactory() {
		super("settings", "settingsPage");
	}

	@Override
	protected boolean isAuthorized() {
		return getUserACL().canShowSettings();
	}
}
