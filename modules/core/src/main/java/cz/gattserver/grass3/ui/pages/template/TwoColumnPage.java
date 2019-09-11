package cz.gattserver.grass3.ui.pages.template;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Div;

public abstract class TwoColumnPage extends BasePage {

	private static final long serialVersionUID = -6878321656448036198L;

	@Override
	protected void createContent(Div layout) {
		Div leftContentLayout = new Div();
		leftContentLayout.setId("left-content");
		leftContentLayout.add(createLeftColumnContent());
		layout.add(leftContentLayout);

		Div rightContentLayout = new Div();
		rightContentLayout.setId("right-content");
		rightContentLayout.add(createRightColumnContent());
		layout.add(rightContentLayout);
	}

	/**
	 * Obsah levé části
	 * 
	 * @return layout
	 */
	protected abstract Component createLeftColumnContent();

	/**
	 * Obsah pravé části
	 * 
	 * @return layout
	 */
	protected abstract Component createRightColumnContent();

}
