package org.myftp.gattserver.grass3.subwindows;

import com.vaadin.server.ThemeResource;

public class InfoSubwindow extends MessageSubwindow {

	private static final long serialVersionUID = -4793025663820815400L;

	public InfoSubwindow(String labelCaption) {
		super("Info", labelCaption, new ThemeResource("img/tags/info_16.png"));
	}

}
