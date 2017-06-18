package cz.gattserver.grass3.pages.template;

import com.vaadin.ui.Component;
import com.vaadin.ui.CustomLayout;

import cz.gattserver.grass3.ui.util.GrassRequest;

public abstract class OneColumnPage extends BasePage {

	public OneColumnPage(GrassRequest request) {
		super(request);
	}

	private static final long serialVersionUID = 5064416476628186307L;

	@Override
	protected void createContent(CustomLayout layout) {
		CustomLayout contentLayout = new CustomLayout("oneColumn");
		layout.addComponent(contentLayout, "content");
		contentLayout.addComponent(createContent(), "content");
	}

	/**
	 * Obsah sloupce
	 * 
	 * @param layout
	 */
	protected abstract Component createContent();

}
