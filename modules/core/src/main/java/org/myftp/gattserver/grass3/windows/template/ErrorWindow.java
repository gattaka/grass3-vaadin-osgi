package org.myftp.gattserver.grass3.windows.template;

import com.vaadin.ui.Label;

public abstract class ErrorWindow extends GrassWindow {

	private static final long serialVersionUID = 3728073040878360420L;

	public ErrorWindow(String name) {
		setName(name);
	}

	@Override
	protected void buildLayout() {
		// TODO - layout, apod.
		addComponent(new Label(getErrorText()));
	}

	@Override
	protected void onShow() {
	}

	protected abstract String getErrorText();

}
