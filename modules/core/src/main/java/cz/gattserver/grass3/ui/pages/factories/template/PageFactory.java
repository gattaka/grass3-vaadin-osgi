package cz.gattserver.grass3.ui.pages.factories.template;

import cz.gattserver.grass3.server.GrassRequest;
import cz.gattserver.grass3.ui.pages.template.GrassPage;

public interface PageFactory {

	public String getPageName();

	public GrassPage createPageIfAuthorized(GrassRequest request);

}
