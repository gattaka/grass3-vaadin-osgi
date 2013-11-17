package org.myftp.gattserver.grass3.medic.web.templates;

import com.vaadin.ui.Button;
import com.vaadin.ui.Component;

public abstract class GrassBtn extends Button {

	private static final long serialVersionUID = 5800605430077988478L;

	public GrassBtn(String caption, Component... triggerComponents) {
		setCaption(caption);
		addClickListener(getClickListener(triggerComponents));
	}

	protected abstract Button.ClickListener getClickListener(
			Component... triggerComponents);

}
