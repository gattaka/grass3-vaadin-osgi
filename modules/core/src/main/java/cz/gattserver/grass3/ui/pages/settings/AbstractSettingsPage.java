package cz.gattserver.grass3.ui.pages.settings;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Div;

import cz.gattserver.grass3.server.GrassRequest;
import cz.gattserver.grass3.ui.pages.template.GrassPage;

public abstract class AbstractSettingsPage extends GrassPage {

	public AbstractSettingsPage(GrassRequest request) {
		super(request);
	}

	@Override
	protected Div createPayload() {
		Div layout = new Div();
		layout.add(createContent());
		return layout;
	}

	protected abstract Component createContent();

}
