package cz.gattserver.grass3.ui.components;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Image;

import cz.gattserver.web.common.ui.ImageIcon;

public class ImageButton extends Button {

	private static final long serialVersionUID = 4204958919924333786L;

	public ImageButton(String caption, ImageIcon icon, ComponentEventListener<ClickEvent<Button>> clickListener) {
		setIcon(new Image(icon.createResource(), caption));
		addClickListener(clickListener);
	}

	public ImageButton(String caption, Image img, ComponentEventListener<ClickEvent<Button>> clickListener) {
		setText(caption);
		setTooltip(caption);
		if (img != null)
			setIcon(img);
		addClickListener(clickListener);
	}

	public ImageButton setTooltip(String value) {
		getElement().setProperty("title", value);
		return this;
	}

	public ImageButton clearText() {
		setText(null);
		return this;
	}

}
