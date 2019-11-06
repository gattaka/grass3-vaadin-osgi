package cz.gattserver.grass3.ui.pages.settings;

import org.springframework.stereotype.Component;

import cz.gattserver.grass3.ui.pages.factories.template.AbstractPageFactory;

@Component("settingsPageFactory")
public class SettingsPageFactory extends AbstractPageFactory {

	public SettingsPageFactory() {
		super("settings");
	}

}
