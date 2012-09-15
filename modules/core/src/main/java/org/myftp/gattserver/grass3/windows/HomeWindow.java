package org.myftp.gattserver.grass3.windows;

import org.myftp.gattserver.grass3.windows.template.OneColumnWindow;

import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Button.ClickEvent;

public class HomeWindow extends OneColumnWindow {

	private static final long serialVersionUID = 2474374292329895766L;

	public static final String NAME = "home";

	public HomeWindow() {
		setName(NAME);
		setCaption("Gattserver");
	}

	@Override
	protected void createContent(HorizontalLayout layout) {
		Button button = new Button("Update", new ClickListener() {
			
			private static final long serialVersionUID = 246988596924399466L;

			public void buttonClick(ClickEvent event) {
				onShow();
			}
		});
		layout.addComponent(button);
	}
}
