package cz.gattserver.grass3.ui.pages.template;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Div;

import cz.gattserver.grass3.server.GrassRequest;

public abstract class OneColumnPage extends BasePage {

	private static final long serialVersionUID = 5541555440277025949L;

	public OneColumnPage(GrassRequest request) {
		super(request);
	}

	@Override
	protected void createContent(Div layout) {
		Div contentLayout = new Div();
		contentLayout.setId("center-content");
		contentLayout.add(createColumnContent());
		layout.add(contentLayout);
	}

	/**
	 * Obsah sloupce
	 * 
	 * @return layout
	 */
	protected abstract Component createColumnContent();

}
