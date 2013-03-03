package org.myftp.gattserver.grass3.pages;

import java.util.List;

import org.myftp.gattserver.grass3.pages.factories.template.AbstractSettingsPageFactory;
import org.myftp.gattserver.grass3.pages.template.TwoColumnPage;
import org.myftp.gattserver.grass3.util.GrassRequest;
import org.springframework.beans.factory.annotation.Autowired;
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

	@Autowired
	public List<AbstractSettingsPageFactory> settingsPageFactories;

	public SettingsPage(GrassRequest request) {
		super(request);
	}

	@Override
	protected Component createLeftColumnContent() {
		VerticalLayout leftColumnLayout = new VerticalLayout();
		leftColumnLayout.setMargin(true);

		for (AbstractSettingsPageFactory settingsPageFactory : settingsPageFactories) {
			Link link = new Link(settingsPageFactory.getSettingsCaption(),
					getPageResource(settingsPageFactory));
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
