package cz.gattserver.grass3.ui.components;

import com.vaadin.ui.Button;

import cz.gattserver.web.common.ui.ImageIcon;

public class CreateButton extends ImageButton {

	private static final long serialVersionUID = -9054113192020716390L;

	public CreateButton(Button.ClickListener clickListener) {
		this("Vytvořit", clickListener);
	}

	public CreateButton(String caption, Button.ClickListener clickListener) {
		super(caption, ImageIcon.PLUS_16_ICON.createResource(), clickListener);
	}

}
