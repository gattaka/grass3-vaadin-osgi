package cz.gattserver.grass3.tabs.factories.template;

import cz.gattserver.grass3.pages.template.GrassLayout;
import cz.gattserver.grass3.ui.util.GrassRequest;

public interface ISettingsTabFactory {

	public String getSettingsCaption();

	public String getSettingsURL();

	public GrassLayout createTabIfAuthorized(GrassRequest request);
	
	public boolean isAuthorized();

}
