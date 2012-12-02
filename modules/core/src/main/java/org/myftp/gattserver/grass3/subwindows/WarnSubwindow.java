package org.myftp.gattserver.grass3.subwindows;

import com.vaadin.terminal.ThemeResource;

public class WarnSubwindow extends MessageSubwindow {

	private static final long serialVersionUID = -4793025663820815400L;

	public WarnSubwindow(String labelCaption) {
		super("Varování", labelCaption, new ThemeResource(
				"img/tags/warning_16.png"));
	}

}
