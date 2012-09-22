package org.myftp.gattserver.grass3.windows.err;

import org.myftp.gattserver.grass3.windows.template.OneColumnWindow;

import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

public class Err500 extends OneColumnWindow {

	private static final long serialVersionUID = 3728073040878360420L;

	public Err500() {
		setName("500");
	}

	@Override
	protected void createContent(VerticalLayout layout) {

		layout.addComponent(new Label("Došlo k chybě na straně serveru"));

	}

}
