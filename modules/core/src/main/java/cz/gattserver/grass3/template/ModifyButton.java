package cz.gattserver.grass3.template;

import com.vaadin.ui.Button;

import cz.gattserver.web.common.ui.ImageIcons;

public class ModifyButton extends ImageButton {

	private static final long serialVersionUID = -9054113192020716390L;

	public ModifyButton(Button.ClickListener clickListener) {
		super("Upravit", ImageIcons.PENCIL_16_ICON, clickListener);
	}

}
