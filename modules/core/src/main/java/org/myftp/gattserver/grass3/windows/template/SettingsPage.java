package org.myftp.gattserver.grass3.windows.template;

import org.myftp.gattserver.grass3.ServiceHolder;
import org.myftp.gattserver.grass3.service.ISettingsService;
import org.myftp.gattserver.grass3.util.GrassRequest;
import org.myftp.gattserver.grass3.windows.ifces.SettingsPageFactory;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.Link;
import com.vaadin.ui.VerticalLayout;

public class SettingsPage extends TwoColumnPage {

	private static final long serialVersionUID = 2474374292329895766L;

	public static final SettingsPageFactory FACTORY = new SettingsPageFactory("settings") {
		@Override
		public SettingsPage createSettingsPage(GrassRequest request) {
			return new SettingsPage(request);
		}
	};
	
	public SettingsPage(GrassRequest request) {
		super(request);
	}

	@Override
	protected Component createLeftColumnContent() {
		VerticalLayout leftColumnLayout = new VerticalLayout();
		leftColumnLayout.setMargin(true);

		for (ISettingsService settingsService : ServiceHolder
				.getSettingsServices()) {
			Link link = new Link(settingsService.getSettingsCaption(),
					getPageResource(settingsService.getSettingsPageFactory()));
			leftColumnLayout.addComponent(link);
		}

		return leftColumnLayout;
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

}
