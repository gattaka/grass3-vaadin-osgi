package org.myftp.gattserver.grass3.fm;

import org.myftp.gattserver.grass3.windows.template.OneColumnWindow;

import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;

public class FMWindow extends OneColumnWindow {

	private static final long serialVersionUID = -950042653154868289L;
	
	public static final String NAME = "fm";
	
	public FMWindow() {
		setName(NAME);
	}

	@Override
	protected void createContent(HorizontalLayout layout) { 
		layout.addComponent(new Label("Halo, tady FM"));
	}

}
