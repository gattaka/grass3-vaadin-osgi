package org.myftp.gattserver.grass3.windows.template;

import org.myftp.gattserver.grass3.ServiceHolder;
import org.myftp.gattserver.grass3.service.ISettingsService;
import org.myftp.gattserver.grass3.util.GrassRequest;
import org.myftp.gattserver.grass3.windows.HomePage;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.Link;
import com.vaadin.ui.VerticalLayout;

public class SettingsPage extends TwoColumnWindow {

	private static final long serialVersionUID = 2474374292329895766L;

	public static enum SettingsPageFactory implements IPageFactory {

		INSTANCE;

		@Override
		public String getPageName() {
			return "settings";
		}

		@Override
		public Component createPage(GrassRequest request) {
			return new SettingsPage(request);
		}
	}

	private VerticalLayout leftColumnLayout;

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
