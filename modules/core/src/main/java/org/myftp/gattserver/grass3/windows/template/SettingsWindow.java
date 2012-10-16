package org.myftp.gattserver.grass3.windows.template;

import org.myftp.gattserver.grass3.util.URLTool;
import org.myftp.gattserver.grass3.windows.UserSettingsWindow;

import com.vaadin.terminal.ExternalResource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Label;
import com.vaadin.ui.Link;
import com.vaadin.ui.VerticalLayout;

public class SettingsWindow extends TwoColumnWindow {

	private static final long serialVersionUID = 2474374292329895766L;

	public static final String NAME = "settings";

	private VerticalLayout leftColumnLayout;

	public SettingsWindow() {
		super.setName(NAME);
		setCaption("Gattserver");
	}

	@Override
	protected void createLeftColumnContent(VerticalLayout layout) {
		leftColumnLayout = layout;
		leftColumnLayout.setMargin(true);
	}

	private void createSettingsMenu() {

		// TODO nahrát ze settingsService karty nastavení
		Link link = new Link("Uživatelé", new ExternalResource(
				URLTool.getWindowURL(getApplication().getURL(),
						UserSettingsWindow.NAME)));

		leftColumnLayout.removeAllComponents();
		leftColumnLayout.addComponent(link);

	}

	@Override
	protected void createRightColumnContent(VerticalLayout layout) {
		Label label = new Label("Zvolte položku nastavení z menu");
		layout.addComponent(label);
		layout.setComponentAlignment(label, Alignment.MIDDLE_CENTER);
		layout.setSpacing(true);
		layout.setMargin(true);
	}

	@Override
	protected void onShow() {
		createSettingsMenu();
		super.onShow();
	}

}
