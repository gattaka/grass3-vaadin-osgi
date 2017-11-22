package cz.gattserver.grass3.ui.pages.settings.factories;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cz.gattserver.grass3.security.CoreACL;
import cz.gattserver.grass3.server.GrassRequest;
import cz.gattserver.grass3.ui.pages.factories.template.AbstractPageFactory;
import cz.gattserver.grass3.ui.pages.settings.SettingsPage;
import cz.gattserver.grass3.ui.pages.template.GrassPage;

@Component("settingsPageFactory")
public class SettingsPageFactory extends AbstractPageFactory {

	private static final long serialVersionUID = 6466620765602543041L;

	@Autowired
	private CoreACL coreACL;

	public SettingsPageFactory() {
		super("settings");
	}

	@Override
	protected boolean isAuthorized() {
		return coreACL.canShowSettings(getUser());
	}

	@Override
	protected GrassPage createPage(GrassRequest request) {
		return new SettingsPage(request);
	}
}
