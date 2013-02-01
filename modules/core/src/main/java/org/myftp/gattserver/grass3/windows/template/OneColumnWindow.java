package org.myftp.gattserver.grass3.windows.template;

import com.vaadin.ui.Component;
import com.vaadin.ui.CustomLayout;

public abstract class OneColumnWindow extends BaseWindow {

	private static final long serialVersionUID = 5064416476628186307L;

	@Override
	protected void createWindowContent(CustomLayout layout) {

		CustomLayout contentLayout = createLayoutFromFile("oneColumn");
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
