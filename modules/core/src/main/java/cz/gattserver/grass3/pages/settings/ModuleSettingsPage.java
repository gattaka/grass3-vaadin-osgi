package cz.gattserver.grass3.pages.settings;

import com.vaadin.ui.Component;
import com.vaadin.ui.CustomLayout;
import com.vaadin.ui.Layout;

import cz.gattserver.grass3.pages.template.GrassPage;
import cz.gattserver.grass3.server.GrassRequest;

public abstract class ModuleSettingsPage extends GrassPage {

	public ModuleSettingsPage(GrassRequest request) {
		super(request);
	}

	@Override
	protected Layout createPayload() {
		CustomLayout layout = new CustomLayout("settings");
		layout.addComponent(createContent(), "content");
		return layout;
	}

	protected abstract Component createContent();

}
