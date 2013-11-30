package org.myftp.gattserver.grass3.pages.factories.template;

import org.myftp.gattserver.grass3.pages.template.GrassLayout;
import org.myftp.gattserver.grass3.ui.util.GrassRequest;

public interface IPageFactory {

	public String getPageName();

	public GrassLayout createPage(GrassRequest request);

}
