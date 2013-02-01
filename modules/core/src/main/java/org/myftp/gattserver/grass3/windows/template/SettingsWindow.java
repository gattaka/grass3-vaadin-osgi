package org.myftp.gattserver.grass3.windows.template;

import org.myftp.gattserver.grass3.ServiceHolder;
import org.myftp.gattserver.grass3.service.ISettingsService;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.Link;
import com.vaadin.ui.VerticalLayout;

public class SettingsWindow extends TwoColumnWindow {

	private static final long serialVersionUID = 2474374292329895766L;

	private VerticalLayout leftColumnLayout;

	public SettingsWindow() {
		super.setName("settings");
		setCaption("Gattserver");
	}

	@Override
	protected Component createLeftColumnContent() {
		leftColumnLayout = new VerticalLayout();
		leftColumnLayout.setMargin(true);
		return leftColumnLayout;
	}

	private void createSettingsMenu() {

		leftColumnLayout.removeAllComponents();

		for (ISettingsService settingsService : ServiceHolder.getInstance()
				.getSettingsServices()) {
			Link link = new Link(settingsService.getSettingsCaption(),
					getWindowResource(settingsService.getSettingsWindowClass()));
			leftColumnLayout.addComponent(link);
		}

	}

	@Override
	protected Component createRightColumnContent() {
		
		VerticalLayout layout = new VerticalLayout();
		
		Label label = new Label("Zvolte položku nastavení z menu");
		layout.addComponent(label);
		layout.setComponentAlignment(label, Alignment.MIDDLE_CENTER);
		layout.setSpacing(true);
		layout.setMargin(true);
		
		return layout;
	}

	@Override
	protected void onShow() {
		createSettingsMenu();
		super.onShow();
	}
}
