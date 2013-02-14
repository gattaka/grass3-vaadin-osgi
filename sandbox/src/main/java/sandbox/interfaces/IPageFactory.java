package sandbox.interfaces;

import com.vaadin.ui.Component;

public interface IPageFactory {

	public String getPageName();

	public Component createPage();

}
