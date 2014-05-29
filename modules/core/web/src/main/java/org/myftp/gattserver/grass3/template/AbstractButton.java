package org.myftp.gattserver.grass3.template;

import com.vaadin.server.Resource;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Button;

public abstract class AbstractButton extends Button {

	private static final long serialVersionUID = 4204958919924333786L;

	public abstract void onClick(ClickEvent event);
	
	public AbstractButton(String caption, String themeImageName) {
		setDescription(caption);
		setIcon((Resource) new ThemeResource(themeImageName));
		addClickListener(new Button.ClickListener() {
			private static final long serialVersionUID = 607422393151282918L;

			public void buttonClick(ClickEvent event) {
				onClick(event);
			}
		});
	}
	
	
}
