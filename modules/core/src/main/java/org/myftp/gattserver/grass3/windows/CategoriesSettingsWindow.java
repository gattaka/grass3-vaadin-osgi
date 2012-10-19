package org.myftp.gattserver.grass3.windows;

import org.myftp.gattserver.grass3.windows.template.SettingsWindow;

import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

public class CategoriesSettingsWindow extends SettingsWindow {

	private static final long serialVersionUID = 2474374292329895766L;

	public CategoriesSettingsWindow() {
		setName("categories-settings");
		setCaption("Gattserver");
	}

	@Override
	protected void createRightColumnContent(VerticalLayout layout) {

		layout.setMargin(true);
		layout.setSpacing(true);

		VerticalLayout usersLayout = new VerticalLayout();
		layout.addComponent(usersLayout);

		usersLayout.addComponent(new Label("<h2>Správa kategorií</h2>",
				Label.CONTENT_XHTML));

	}

	@Override
	protected void onShow() {

		super.onShow();
	}

}
