package cz.gattserver.grass3.pages;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.Link;
import com.vaadin.ui.VerticalLayout;

import cz.gattserver.grass3.pages.factories.template.PageFactory;
import cz.gattserver.grass3.pages.template.TwoColumnPage;
import cz.gattserver.grass3.tabs.factories.template.SettingsTabFactory;
import cz.gattserver.grass3.ui.util.GrassRequest;
import cz.gattserver.grass3.ui.util.SettingsTabFactoriesRegister;
import cz.gattserver.web.common.URLPathAnalyzer;

public class SettingsPage extends TwoColumnPage {

	private static final long serialVersionUID = 2474374292329895766L;

	@Autowired
	private List<SettingsTabFactory> settingsTabFactories;

	@Resource(name = "settingsPageFactory")
	private PageFactory settingsPageFactory;

	@Autowired
	private SettingsTabFactoriesRegister settingsTabFactoriesRegister;

	private SettingsTabFactory settingsTabFactory = null;

	public SettingsPage(GrassRequest request) {
		super(request);
	}

	@Override
	protected void init() {
		URLPathAnalyzer analyzer = getRequest().getAnalyzer();
		String settingsTabName = analyzer.getCurrentPathToken();
		SettingsTabFactory settingsTabFactory = settingsTabFactoriesRegister.getFactory(settingsTabName);

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
		VerticalLayout marginLayout = new VerticalLayout();
		marginLayout.setMargin(true);
		
		VerticalLayout leftColumnLayout = new VerticalLayout();
		leftColumnLayout.setMargin(true);
		marginLayout.addComponent(leftColumnLayout);

		for (SettingsTabFactory settingsTabFactory : settingsTabFactories) {
			Link link = new Link(settingsTabFactory.getSettingsCaption(),
					getPageResource(settingsPageFactory, settingsTabFactory.getSettingsURL()));
			leftColumnLayout.addComponent(link);
		}

		return marginLayout;
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
