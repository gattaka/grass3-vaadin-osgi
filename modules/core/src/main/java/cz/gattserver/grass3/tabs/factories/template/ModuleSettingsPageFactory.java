package cz.gattserver.grass3.tabs.factories.template;

import cz.gattserver.grass3.pages.template.GrassPage;
import cz.gattserver.grass3.ui.util.GrassRequest;

public interface ModuleSettingsPageFactory {

	public String getSettingsCaption();

	public String getSettingsURL();

	public GrassPage createPageIfAuthorized(GrassRequest request);
	
	public boolean isAuthorized();

}
