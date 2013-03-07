package org.myftp.gattserver.grass3.modtest;

import org.myftp.gattserver.grass3.tabs.factories.template.SettingsTabFactory;
import org.springframework.stereotype.Component;

@Component("modTestSettingsTabFactory")
public class ModTestSettingsTabFactory extends SettingsTabFactory {

	public ModTestSettingsTabFactory() {
		super("Test modularity", "modTest", "modTestSettingsTab");
	}
}
