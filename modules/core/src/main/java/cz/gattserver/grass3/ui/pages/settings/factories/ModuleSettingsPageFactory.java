package cz.gattserver.grass3.ui.pages.settings.factories;

import cz.gattserver.grass3.ui.pages.template.GrassPage;

public interface ModuleSettingsPageFactory {

	public String getSettingsCaption();

	public String getSettingsURL();

	public GrassPage createPageIfAuthorized();

	public boolean isAuthorized();

}
