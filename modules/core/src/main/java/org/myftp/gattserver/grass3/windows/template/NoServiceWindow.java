package org.myftp.gattserver.grass3.windows.template;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

public class NoServiceWindow extends OneColumnWindow {

	private static final long serialVersionUID = 8907394744054280981L;

	@Override
	protected void createContent(VerticalLayout layout) {
		Label label = new Label("Chybí služba pro čtení tohoto typu obsahu");
		layout.addComponent(label);
		layout.setComponentAlignment(label, Alignment.MIDDLE_CENTER);
		
		layout.setMargin(true);
		layout.setSizeFull();
	}

}
