package org.myftp.gattserver.grass3.subwindows;

import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.ui.Window;

public class GrassSubWindow extends Window {

	private static final long serialVersionUID = -9184044674542039306L;

	public GrassSubWindow(String name) {
		super(name);
		addAction(new Window.CloseShortcut(this, KeyCode.ESCAPE));
	}

}
