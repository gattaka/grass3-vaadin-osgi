package cz.gattserver.grass3.pages.factories.template;

import java.io.Serializable;

import cz.gattserver.grass3.pages.template.GrassLayout;
import cz.gattserver.grass3.ui.util.GrassRequest;

public interface PageFactory extends Serializable {

	public String getPageName();

	public GrassLayout createPageIfAuthorized(GrassRequest request);

}
