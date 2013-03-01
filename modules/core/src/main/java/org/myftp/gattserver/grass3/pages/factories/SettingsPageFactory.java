package org.myftp.gattserver.grass3.pages.factories;

import org.myftp.gattserver.grass3.pages.factories.template.PageFactory;
import org.springframework.stereotype.Component;

@Component("settingsPageFactory")
public class SettingsPageFactory extends PageFactory {

	public SettingsPageFactory() {
		super("settings", "settingsPage");
	}

}
