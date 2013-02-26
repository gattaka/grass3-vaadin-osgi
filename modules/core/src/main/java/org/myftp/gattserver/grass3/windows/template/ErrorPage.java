package org.myftp.gattserver.grass3.windows.template;

import com.vaadin.ui.CustomLayout;
import com.vaadin.ui.Label;

public abstract class ErrorPage extends GrassPage {

	private static final long serialVersionUID = 3728073040878360420L;

	@Override
	protected void createQuotes(CustomLayout layout) {
	}

	@Override
	protected void createContent(CustomLayout layout) {
		layout.addComponent(new Label(getErrorText()), "content");
	}

	@Override
	protected void createMenu(CustomLayout layout) {
	}

	protected abstract String getErrorText();

}
