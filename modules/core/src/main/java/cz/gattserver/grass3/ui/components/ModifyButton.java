package cz.gattserver.grass3.ui.components;

import com.vaadin.ui.Button;

import cz.gattserver.web.common.ui.ImageIcon;

public class ModifyButton extends ImageButton {

	private static final long serialVersionUID = -9054113192020716390L;

	public ModifyButton(Button.ClickListener clickListener) {
		this("Upravit", clickListener);
	}

	public ModifyButton(String caption, Button.ClickListener clickListener) {
		super(caption, ImageIcon.PENCIL_16_ICON.createResource(), clickListener);
	}

}
