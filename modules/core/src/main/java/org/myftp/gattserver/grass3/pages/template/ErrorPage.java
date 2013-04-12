package org.myftp.gattserver.grass3.pages.template;

import org.myftp.gattserver.grass3.util.GrassRequest;

import com.vaadin.ui.CustomLayout;
import com.vaadin.ui.Label;

public abstract class ErrorPage extends AbstractGrassPage {

	private static final long serialVersionUID = 3728073040878360420L;

	public ErrorPage(GrassRequest request) {
		super(request);
	}

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
