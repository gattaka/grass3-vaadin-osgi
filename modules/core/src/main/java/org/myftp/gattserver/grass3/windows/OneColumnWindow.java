package org.myftp.gattserver.grass3.windows;

import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.VerticalLayout;

public abstract class OneColumnWindow extends BaseWindow {

	private static final long serialVersionUID = 5064416476628186307L;

	public OneColumnWindow() {
	}

	@Override
	protected void createWindowContent(AbsoluteLayout layout) {

		VerticalLayout backgroundLayout = new ColumnBuilder(990, "full_right_middle_background") {

			@Override
			protected void createColumnContent(HorizontalLayout layout) {
				createContent(layout);
			}

		}.buildColumn();

		layout.addComponent(backgroundLayout, "left:0px; top:135px;");

	}

	/**
	 * Obsah sloupce
	 * 
	 * @param layout
	 */
	protected abstract void createContent(HorizontalLayout layout);

}
