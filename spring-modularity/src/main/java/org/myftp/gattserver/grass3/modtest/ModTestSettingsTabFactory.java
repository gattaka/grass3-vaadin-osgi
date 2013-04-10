package org.myftp.gattserver.grass3.modtest;

import org.myftp.gattserver.grass3.tabs.factories.template.AbstractSettingsTabFactory;
import org.springframework.stereotype.Component;

@Component("modTestSettingsTabFactory")
public class ModTestSettingsTabFactory extends AbstractSettingsTabFactory {

	public ModTestSettingsTabFactory() {
		super("Test modularity", "modTest", "modTestSettingsTab");
	}
}
