package cz.gattserver.grass3.ui.pages.template;

import com.vaadin.ui.Component;
import com.vaadin.ui.CustomLayout;

import cz.gattserver.grass3.server.GrassRequest;

public abstract class OneColumnPage extends BasePage {

	public OneColumnPage(GrassRequest request) {
		super(request);
	}

	@Override
	protected void createContent(CustomLayout layout) {
		CustomLayout contentLayout = new CustomLayout("oneColumn");
		layout.addComponent(contentLayout, "content");
		contentLayout.addComponent(createContent(), "content");
	}

	/**
	 * Obsah sloupce
	 * 
	 * @return layout
	 */
	protected abstract Component createContent();

}
