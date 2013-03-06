package org.myftp.gattserver.grass3.pages;

import java.util.List;

import javax.annotation.Resource;

import org.myftp.gattserver.grass3.pages.factories.SettingsPageFactory;
import org.myftp.gattserver.grass3.pages.factories.template.SettingsTabFactory;
import org.myftp.gattserver.grass3.pages.template.TwoColumnPage;
import org.myftp.gattserver.grass3.util.GrassRequest;
import org.myftp.gattserver.grass3.util.SettingsTabFactoriesRegister;
import org.myftp.gattserver.grass3.util.URLPathAnalyzer;
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
	private List<SettingsTabFactory> settingsTabFactories;

	@Resource(name = "settingsPageFactory")
	private SettingsPageFactory settingsPageFactory;

	@Resource(name = "settingsTabFactoriesRegister")
	private SettingsTabFactoriesRegister settingsTabFactoriesRegister;

	public SettingsPage(GrassRequest request) {
		super(request);
	}

	@Override
	protected Component createLeftColumnContent() {
		VerticalLayout leftColumnLayout = new VerticalLayout();
		leftColumnLayout.setMargin(true);

		for (SettingsTabFactory settingsTabFactory : settingsTabFactories) {
			Link link = new Link(settingsTabFactory.getSettingsCaption(),
					getPageResource(settingsPageFactory,
							settingsTabFactory.getSettingsURL()));
			leftColumnLayout.addComponent(link);
		}

		return leftColumnLayout;
	}

	@Override
	protected Component createRightColumnContent() {

		URLPathAnalyzer analyzer = getRequest().getAnalyzer();
		String settingsTabName = analyzer.getCurrentPathToken();
		SettingsTabFactory settingsTabFactory = settingsTabFactoriesRegister
				.getFactory(settingsTabName);

		if (settingsTabFactory != null)
			return settingsTabFactory.createPage(getRequest());

		VerticalLayout layout = new VerticalLayout();

		Label label = new Label("Zvolte položku nastavení z menu");
		layout.addComponent(label);
		layout.setComponentAlignment(label, Alignment.MIDDLE_CENTER);
		layout.setSpacing(true);
		layout.setMargin(true);

		return layout;
	}

}
