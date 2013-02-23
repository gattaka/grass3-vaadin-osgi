package org.myftp.gattserver.grass3.windows.template;

import org.myftp.gattserver.grass3.util.GrassRequest;

import com.vaadin.ui.Component;
import com.vaadin.ui.CustomLayout;

public abstract class TwoColumnPage extends BasePage {

	public TwoColumnPage(GrassRequest request) {
		super(request);
	}

	private static final long serialVersionUID = 5064416476628186307L;

	@Override
	protected void createContent(CustomLayout layout) {

		CustomLayout contentLayout = new CustomLayout("twoColumn");
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
