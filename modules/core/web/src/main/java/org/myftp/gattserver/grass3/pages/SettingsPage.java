package org.myftp.gattserver.grass3.pages;

import java.util.List;

import javax.annotation.Resource;

import org.myftp.gattserver.grass3.pages.factories.template.IPageFactory;
import org.myftp.gattserver.grass3.pages.template.TwoColumnPage;
import org.myftp.gattserver.grass3.tabs.factories.template.ISettingsTabFactory;
import org.myftp.gattserver.grass3.ui.util.GrassRequest;
import org.myftp.gattserver.grass3.ui.util.ISettingsTabFactoriesRegister;
import org.myftp.gattserver.grass3.util.URLPathAnalyzer;
import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.Link;
import com.vaadin.ui.VerticalLayout;

public class SettingsPage extends TwoColumnPage {

	private static final long serialVersionUID = 2474374292329895766L;

	@Autowired
	private List<ISettingsTabFactory> settingsTabFactories;

	@Resource(name = "settingsPageFactory")
	private IPageFactory settingsPageFactory;

	@Resource(name = "settingsTabFactoriesRegister")
	private ISettingsTabFactoriesRegister settingsTabFactoriesRegister;

	private ISettingsTabFactory settingsTabFactory = null;

	public SettingsPage(GrassRequest request) {
		super(request);
	}

	@Override
	protected void init() {
		URLPathAnalyzer analyzer = getRequest().getAnalyzer();
		String settingsTabName = analyzer.getCurrentPathToken();
		ISettingsTabFactory settingsTabFactory = settingsTabFactoriesRegister
				.getFactory(settingsTabName);

		if (settingsTabFactory != null) {
			if (settingsTabFactory.isAuthorized() == false) {
				showError403();
				return;
			} else {
				this.settingsTabFactory = settingsTabFactory;
			}
		}
		
		super.init();
	}

	@Override
	protected Component createLeftColumnContent() {
		VerticalLayout leftColumnLayout = new VerticalLayout();
		leftColumnLayout.setMargin(true);

		for (ISettingsTabFactory settingsTabFactory : settingsTabFactories) {
			Link link = new Link(settingsTabFactory.getSettingsCaption(),
					getPageResource(settingsPageFactory,
							settingsTabFactory.getSettingsURL()));
			leftColumnLayout.addComponent(link);
		}

		return leftColumnLayout;
	}

	@Override
	protected Component createRightColumnContent() {

		// pokud není pageFactory prázdná, pak se zobrazuje konkrétní nastavení
		if (settingsTabFactory != null)
			return settingsTabFactory.createTabIfAuthorized(getRequest());

		// jinak zobraz info o nabídce
		VerticalLayout layout = new VerticalLayout();

		Label label = new Label("Zvolte položku nastavení z menu");
		layout.addComponent(label);
		layout.setComponentAlignment(label, Alignment.MIDDLE_CENTER);
		layout.setSpacing(true);
		layout.setMargin(true);

		return layout;
	}

}
