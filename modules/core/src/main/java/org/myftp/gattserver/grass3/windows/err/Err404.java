package org.myftp.gattserver.grass3.windows.err;

import org.myftp.gattserver.grass3.windows.template.OneColumnWindow;

import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

public class Err404 extends OneColumnWindow {

	private static final long serialVersionUID = 3728073040878360420L;

	public static final String NAME = "404";
	
	public Err404() {
		setName(NAME);
	}

	@Override
	protected void createContent(VerticalLayout layout) {

		layout.addComponent(new Label("Stránka nebyla nalezena"));

	}

}
