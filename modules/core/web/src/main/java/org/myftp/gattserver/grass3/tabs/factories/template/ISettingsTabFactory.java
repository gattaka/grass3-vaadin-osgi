package org.myftp.gattserver.grass3.tabs.factories.template;

import org.myftp.gattserver.grass3.pages.template.GrassLayout;
import org.myftp.gattserver.grass3.ui.util.GrassRequest;

public interface ISettingsTabFactory {

	public String getSettingsCaption();

	public String getSettingsURL();

	public GrassLayout createPage(GrassRequest request);
	
	public boolean isAuthorized();

}
