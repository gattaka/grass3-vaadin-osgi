package cz.gattserver.grass3.tabs.template;

import com.vaadin.ui.Component;

import cz.gattserver.grass3.pages.template.GrassLayout;
import cz.gattserver.grass3.ui.util.GrassRequest;

public abstract class AbstractSettingsTab extends GrassLayout implements SettingsTab {

	private static final long serialVersionUID = 1135599178354473520L;

	public AbstractSettingsTab(GrassRequest request) {
		super("settings", request);
		init();
	}

	private void init() {
		addComponent(createContent(), "content");
	}

	protected abstract Component createContent();

	public GrassLayout getContent() {
		return this;
	}

}
