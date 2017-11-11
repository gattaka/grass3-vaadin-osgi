package cz.gattserver.grass3.pages.settings.factories;

import cz.gattserver.grass3.pages.template.GrassPage;
import cz.gattserver.grass3.server.GrassRequest;

public interface ModuleSettingsPageFactory {

	public String getSettingsCaption();

	public String getSettingsURL();

	public GrassPage createPageIfAuthorized(GrassRequest request);
	
	public boolean isAuthorized();

}
