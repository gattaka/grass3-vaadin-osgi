package cz.gattserver.grass3.template;

import cz.gattserver.web.common.ui.ImageIcons;

public abstract class CreateButton extends AbstractButton {

	private static final long serialVersionUID = -9054113192020716390L;

	public CreateButton() {
		super("Vytvořit", ImageIcons.PLUS_16_ICON);
	}

}
