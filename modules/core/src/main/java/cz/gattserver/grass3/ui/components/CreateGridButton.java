package cz.gattserver.grass3.ui.components;

import com.vaadin.ui.Button;

import cz.gattserver.web.common.ui.ImageIcon;

public class CreateGridButton extends Button {

	private static final long serialVersionUID = -5924239277930098183L;

	public CreateGridButton(String caption, Button.ClickListener clickListener) {
		super(caption, clickListener);
		setIcon(ImageIcon.PLUS_16_ICON.createResource());
	}

}
