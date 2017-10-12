package cz.gattserver.grass3.template;

import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Button;

import cz.gattserver.web.common.ui.ImageIcons;

public class CreateGridButton extends Button {

	private static final long serialVersionUID = -5924239277930098183L;

	public CreateGridButton(String caption, Button.ClickListener clickListener) {
		super(caption, clickListener);
		setIcon(new ThemeResource(ImageIcons.PLUS_16_ICON));
	}

}
