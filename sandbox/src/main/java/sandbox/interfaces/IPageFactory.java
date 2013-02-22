package sandbox.interfaces;

import sandbox.util.GrassRequest;

import com.vaadin.ui.Component;

public interface IPageFactory {

	public String getPageName();

	public Component createPage(GrassRequest request);

}
