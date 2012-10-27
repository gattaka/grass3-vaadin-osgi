package org.myftp.gattserver.grass3.windows;

import org.myftp.gattserver.grass3.windows.template.GrassWindow;

public class LogoutWindow extends GrassWindow {

	private static final long serialVersionUID = 8276040419934174157L;

	public static final String NAME = "logout";

	public LogoutWindow() {
		setName(NAME);
		setCaption("Logout");
	}

	@Override
	protected void onShow() {
		getApplication().close();
	}

	@Override
	protected void buildLayout() {
	}

}
