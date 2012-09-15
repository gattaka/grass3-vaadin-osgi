package org.myftp.gattserver.grass3.windows;

import org.myftp.gattserver.grass3.windows.template.OneColumnWindow;

import com.vaadin.ui.HorizontalLayout;

public class HomeWindow extends OneColumnWindow {

	private static final long serialVersionUID = 2474374292329895766L;

	public static final String NAME = "home";

	public HomeWindow() {
		setName(NAME);
		setCaption("Gattserver");
	}

	@Override
	protected void createContent(HorizontalLayout layout) {
		// TODO Auto-generated method stub

	}
}
