package org.myftp.gattserver.grass3.pages;

import org.myftp.gattserver.grass3.ServiceHolder;
import org.myftp.gattserver.grass3.pages.template.TwoColumnPage;
import org.myftp.gattserver.grass3.service.ISettingsService;
import org.myftp.gattserver.grass3.util.GrassRequest;
import org.springframework.context.annotation.Scope;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.Link;
import com.vaadin.ui.VerticalLayout;

@org.springframework.stereotype.Component("settingsPage")
@Scope("prototype")
public class SettingsPage extends TwoColumnPage {

	private static final long serialVersionUID = 2474374292329895766L;

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
