package org.myftp.gattserver.grass3.windows.template;


import com.vaadin.ui.Link;
import com.vaadin.ui.VerticalLayout;

public abstract class SettingsWindow extends TwoColumnWindow {

	private static final long serialVersionUID = 2474374292329895766L;

	private VerticalLayout leftColumnLayout;

	public SettingsWindow() {
		setName("settings");
		setCaption("Gattserver");
	}

	@Override
	protected void createLeftColumnContent(VerticalLayout layout) {
		leftColumnLayout = layout;
	}

	private void createSettingsMenu() {

		// TODO nahrát ze settingsService karty nastavení
		Link link = new Link();
		
	}

	@Override
	protected void onShow() {
		createSettingsMenu();
		super.onShow();
	}

}
