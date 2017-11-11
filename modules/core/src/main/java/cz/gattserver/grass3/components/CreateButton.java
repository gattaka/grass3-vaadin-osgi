package cz.gattserver.grass3.components;

import com.vaadin.ui.Button;

import cz.gattserver.web.common.ui.ImageIcons;

public class CreateButton extends ImageButton {

	private static final long serialVersionUID = -9054113192020716390L;

	public CreateButton(Button.ClickListener clickListener) {
		super("Vytvořit", ImageIcons.PLUS_16_ICON, clickListener);
	}

}
