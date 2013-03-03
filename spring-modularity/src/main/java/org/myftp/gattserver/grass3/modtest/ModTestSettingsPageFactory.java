package org.myftp.gattserver.grass3.modtest;

import org.myftp.gattserver.grass3.pages.factories.template.AbstractSettingsPageFactory;
import org.springframework.stereotype.Component;

@Component("modTestSettingsPageFactory")
public class ModTestSettingsPageFactory extends AbstractSettingsPageFactory {

	public ModTestSettingsPageFactory() {
		super("modTest", "modTestSettingsPage");
	}

	@Override
	public String getSettingsCaption() {
		return "modTest";
	}

}
