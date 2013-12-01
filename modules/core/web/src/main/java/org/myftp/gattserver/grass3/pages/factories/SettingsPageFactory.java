package org.myftp.gattserver.grass3.pages.factories;

import javax.annotation.Resource;

import org.myftp.gattserver.grass3.pages.factories.template.AbstractPageFactory;
import org.myftp.gattserver.grass3.security.ICoreACL;
import org.springframework.stereotype.Component;

@Component("settingsPageFactory")
public class SettingsPageFactory extends AbstractPageFactory {

	private static final long serialVersionUID = 6466620765602543041L;

	@Resource(name = "coreACL")
	private ICoreACL coreACL;

	public SettingsPageFactory() {
		super("settings", "settingsPage");
	}

	@Override
	protected boolean isAuthorized() {
		return coreACL.canShowSettings(getUser());
	}
}
