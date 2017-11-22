package cz.gattserver.grass3.ui.components;

import com.vaadin.ui.Button;

import cz.gattserver.web.common.ui.ImageIcons;

public class DeleteButton extends ImageButton {

	private static final long serialVersionUID = -9054113192020716390L;

	public DeleteButton(Button.ClickListener clickListener) {
		super("Smazat", ImageIcons.DELETE_16_ICON, clickListener);
	}

}
