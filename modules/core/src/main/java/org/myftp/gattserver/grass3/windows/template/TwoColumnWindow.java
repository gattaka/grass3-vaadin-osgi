package org.myftp.gattserver.grass3.windows.template;

import com.vaadin.ui.Component;
import com.vaadin.ui.CustomLayout;

public abstract class TwoColumnWindow extends BaseWindow {

	private static final long serialVersionUID = 5064416476628186307L;

	@Override
	protected void createWindowContent(CustomLayout layout) {

		CustomLayout contentLayout = createLayoutFromFile("twoColumn");
		layout.addComponent(contentLayout, "content");

		contentLayout.addComponent(createLeftColumnContent(), "leftcontent");
		contentLayout.addComponent(createRightColumnContent(), "rightcontent");
	}

	/**
	 * Obsah levé části
	 * 
	 * @param layout
	 */
	protected abstract Component createLeftColumnContent();

	/**
	 * Obsah pravé části
	 * 
	 * @param layout
	 */
	protected abstract Component createRightColumnContent();

}
