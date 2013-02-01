package sandbox;

import com.vaadin.ui.VerticalLayout;

public abstract class OneColumnWindow extends BaseWindow {

	private static final long serialVersionUID = 5064416476628186307L;

	@Override
	protected void createWindowContent(VerticalLayout layout) {

		VerticalLayout spacingLayout = new VerticalLayout();
		spacingLayout.setHeight("10px");
		layout.addComponent(spacingLayout);

		createContent(layout);

	}

	/**
	 * Obsah sloupce
	 * 
	 * @param layout
	 */
	protected abstract void createContent(VerticalLayout layout);

}
