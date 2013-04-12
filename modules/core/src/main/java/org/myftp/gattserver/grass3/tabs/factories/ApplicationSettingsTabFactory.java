package org.myftp.gattserver.grass3.tabs.factories;

import org.myftp.gattserver.grass3.tabs.factories.template.AbstractSettingsTabFactory;
import org.springframework.stereotype.Component;

@Component("applicationSettingsTabFactory")
public class ApplicationSettingsTabFactory extends AbstractSettingsTabFactory {

	public ApplicationSettingsTabFactory() {
		super("Aplikace", "app", "applicationSettingsTab");
	}

	@Override
	protected boolean isAuthorized() {
		return getUserACL().canShowApplicationSettings();
	}

}
