package cz.gattserver.grass3.ui.pages.settings;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Div;

import cz.gattserver.grass3.ui.pages.template.GrassPage;

public abstract class AbstractSettingsPage extends GrassPage {

	private static final long serialVersionUID = 1226081678716885486L;

	@Override
	protected Div createPayload() {
		Div layout = new Div();
		layout.add(createContent());
		return layout;
	}

	protected abstract Component createContent();

}
