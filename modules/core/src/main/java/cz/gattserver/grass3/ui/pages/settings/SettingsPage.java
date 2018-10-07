package cz.gattserver.grass3.ui.pages.settings;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.Layout;
import com.vaadin.ui.Link;
import com.vaadin.ui.VerticalLayout;

import cz.gattserver.grass3.exception.GrassPageException;
import cz.gattserver.grass3.modules.register.ModuleSettingsPageFactoriesRegister;
import cz.gattserver.grass3.server.GrassRequest;
import cz.gattserver.grass3.ui.pages.factories.template.PageFactory;
import cz.gattserver.grass3.ui.pages.settings.factories.ModuleSettingsPageFactory;
import cz.gattserver.grass3.ui.pages.template.TwoColumnPage;
import cz.gattserver.web.common.server.URLPathAnalyzer;

public class SettingsPage extends TwoColumnPage {

	@Autowired
	private List<ModuleSettingsPageFactory> settingsTabFactories;

	@Resource(name = "settingsPageFactory")
	private PageFactory settingsPageFactory;

	@Autowired
	private ModuleSettingsPageFactoriesRegister register;

	private ModuleSettingsPageFactory settingsTabFactory = null;

	public SettingsPage(GrassRequest request) {
		super(request);
	}

	@Override
	protected Layout createPayload() {
		URLPathAnalyzer analyzer = getRequest().getAnalyzer();
		String settingsTabName = analyzer.getCurrentPathToken();
		ModuleSettingsPageFactory moduleSettingsPageFactory = register.getFactory(settingsTabName);

		if (moduleSettingsPageFactory != null) {
			if (moduleSettingsPageFactory.isAuthorized() == false) {
				throw new GrassPageException(403);
			} else {
				this.settingsTabFactory = moduleSettingsPageFactory;
			}
		}

		return super.createPayload();
	}

	@Override
	protected Component createLeftColumnContent() {
		VerticalLayout marginLayout = new VerticalLayout();
		marginLayout.setMargin(true);

		VerticalLayout leftColumnLayout = new VerticalLayout();
		leftColumnLayout.setMargin(true);
		marginLayout.addComponent(leftColumnLayout);

		settingsTabFactories.sort((a, b) -> a.getSettingsCaption().compareTo(b.getSettingsCaption()));
		for (ModuleSettingsPageFactory settingsTabFactory : settingsTabFactories) {
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
			return settingsTabFactory.createPageIfAuthorized(getRequest()).getContent();

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
