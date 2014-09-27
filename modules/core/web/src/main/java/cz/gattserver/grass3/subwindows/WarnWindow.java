package cz.gattserver.grass3.subwindows;

import com.vaadin.server.ThemeResource;

public class WarnWindow extends MessageWindow {

	private static final long serialVersionUID = -4793025663820815400L;

	public WarnWindow(String labelCaption) {
		super("Varování", labelCaption, new ThemeResource(
				"img/tags/warning_16.png"));
	}

}
