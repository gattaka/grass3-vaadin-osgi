package cz.gattserver.grass3.ui.pages.settings;

import com.vaadin.ui.Component;
import com.vaadin.ui.CustomLayout;
import com.vaadin.ui.Layout;

import cz.gattserver.grass3.server.GrassRequest;
import cz.gattserver.grass3.ui.pages.template.GrassPage;

public abstract class AbstractSettingsPage extends GrassPage {

	public AbstractSettingsPage(GrassRequest request) {
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