package org.myftp.gattserver.grass3.pages.factories;

import javax.annotation.Resource;

import org.myftp.gattserver.grass3.pages.SettingsPage;
import org.myftp.gattserver.grass3.pages.factories.template.AbstractPageFactory;
import org.myftp.gattserver.grass3.pages.template.IGrassPage;
import org.myftp.gattserver.grass3.security.ICoreACL;
import org.myftp.gattserver.grass3.ui.util.GrassRequest;
import org.springframework.stereotype.Component;

@Component("settingsPageFactory")
public class SettingsPageFactory extends AbstractPageFactory {

	private static final long serialVersionUID = 6466620765602543041L;

	@Resource(name = "coreACL")
	private ICoreACL coreACL;

	public SettingsPageFactory() {
		super("settings");
	}

	@Override
	protected boolean isAuthorized() {
		return coreACL.canShowSettings(getUser());
	}

	@Override
	protected IGrassPage createPage(GrassRequest request) {
		return new SettingsPage(request);
	}
}
