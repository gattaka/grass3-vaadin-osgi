package org.myftp.gattserver.grass3.modtest;

import org.myftp.gattserver.grass3.pages.factories.template.SettingsTabFactory;
import org.springframework.stereotype.Component;

@Component("modTestSettingsTabFactory")
public class ModTestSettingsTabFactory extends SettingsTabFactory {

	public ModTestSettingsTabFactory() {
		super("Test modularity", "modTest", "modTestSettingsTab");
	}
}
