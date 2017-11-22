package cz.gattserver.grass3.ui.components;

import com.vaadin.server.Resource;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Button;

public class ImageButton extends Button {

	private static final long serialVersionUID = 4204958919924333786L;

	public ImageButton(String caption, Resource imageResource, Button.ClickListener clickListener) {
		setDescription(caption);
		setCaption(caption);
		if (imageResource != null)
			setIcon(imageResource);
		addClickListener(clickListener);
	}

	public ImageButton(String caption, String themeImageName, Button.ClickListener clickListener) {
		this(caption, themeImageName == null ? null : new ThemeResource(themeImageName), clickListener);
	}

}
