package org.myftp.gattserver.grass3.windows.template;

import org.myftp.gattserver.grass3.util.GrassRequest;

import com.vaadin.ui.Component;

public interface IPageFactory {

	public String getPageName();

	public Component createPage(GrassRequest request);

}
