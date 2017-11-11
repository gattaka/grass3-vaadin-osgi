package cz.gattserver.grass3.pages.template;

import com.vaadin.ui.Component;
import com.vaadin.ui.CustomLayout;

import cz.gattserver.grass3.server.GrassRequest;

public abstract class TwoColumnPage extends BasePage {

	public TwoColumnPage(GrassRequest request) {
		super(request);
	}

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
