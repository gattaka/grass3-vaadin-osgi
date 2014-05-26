package org.myftp.gattserver.grass3.pages.factories.template;

import java.io.Serializable;

import org.myftp.gattserver.grass3.pages.template.GrassLayout;
import org.myftp.gattserver.grass3.ui.util.GrassRequest;

public interface IPageFactory extends Serializable {

	public String getPageName();

	public GrassLayout createPageIfAuthorized(GrassRequest request);

}
