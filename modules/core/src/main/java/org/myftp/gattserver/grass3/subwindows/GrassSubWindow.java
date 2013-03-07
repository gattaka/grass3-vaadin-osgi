package org.myftp.gattserver.grass3.subwindows;

import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.ui.Component;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

public class GrassSubWindow extends Window {

	private static final long serialVersionUID = -9184044674542039306L;

	private VerticalLayout layout = new VerticalLayout();
	
	public GrassSubWindow(String name) {
		super(name);

		setContent(layout);
		
		layout.setSpacing(true);
		layout.setMargin(true);

		addAction(new Window.CloseShortcut(this, KeyCode.ESCAPE));

		center();
		setWidth("220px");
	}
	
	protected void addComponent(Component component) {
		layout.addComponent(component);
	}
}
