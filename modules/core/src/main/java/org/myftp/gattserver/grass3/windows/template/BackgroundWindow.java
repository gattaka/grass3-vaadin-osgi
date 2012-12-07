package org.myftp.gattserver.grass3.windows.template;

import com.vaadin.ui.VerticalLayout;

public abstract class BackgroundWindow extends GrassWindow {

	private static final long serialVersionUID = 2474374292329895766L;

	protected void buildLayout() {

		// Hlavní layout - nosič pozadí a rovnoměrného rozsazení elementů
		VerticalLayout backgroundLayout = new VerticalLayout();
		setContent(backgroundLayout);
		backgroundLayout.setStyleName("background_layout");
		backgroundLayout.setWidth("100%");

		VerticalLayout layout = new VerticalLayout();
		backgroundLayout.addComponent(layout);
		layout.setStyleName("layout");
		layout.setWidth("100%");

		buildLayout(layout);

	}

	protected abstract void buildLayout(VerticalLayout layout);

}
