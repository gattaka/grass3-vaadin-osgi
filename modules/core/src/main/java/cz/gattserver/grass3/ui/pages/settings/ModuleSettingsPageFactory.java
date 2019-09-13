package cz.gattserver.grass3.ui.pages.settings;

import com.vaadin.flow.component.html.Div;

public interface ModuleSettingsPageFactory {

	public String getSettingsCaption();

	public String getSettingsURL();

	public void createFragmentIfAuthorized(Div layout);

	public boolean isAuthorized();

}
