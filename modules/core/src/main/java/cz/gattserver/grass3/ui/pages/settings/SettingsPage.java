package cz.gattserver.grass3.ui.pages.settings;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import cz.gattserver.grass3.exception.GrassPageException;
import cz.gattserver.grass3.modules.register.ModuleSettingsPageFactoriesRegister;
import cz.gattserver.grass3.ui.pages.settings.factories.ModuleSettingsPageFactory;
import cz.gattserver.grass3.ui.pages.template.TwoColumnPage;

public class SettingsPage extends TwoColumnPage implements HasUrlParameter<String> {

	private static final long serialVersionUID = 935609806962179905L;

	@Autowired
	private List<ModuleSettingsPageFactory> settingsTabFactories;

	@Autowired
	private ModuleSettingsPageFactoriesRegister register;

	private ModuleSettingsPageFactory settingsTabFactory = null;

	private String moduleParameter;

	@Override
	public void setParameter(BeforeEvent event, String parameter) {
		moduleParameter = parameter;
	}

	@Override
	protected Div createPayload() {
		ModuleSettingsPageFactory moduleSettingsPageFactory = register.getFactory(moduleParameter);

		if (moduleSettingsPageFactory != null) {
			if (!moduleSettingsPageFactory.isAuthorized()) {
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
		marginLayout.setPadding(true);

		VerticalLayout leftColumnLayout = new VerticalLayout();
		leftColumnLayout.setPadding(true);
		marginLayout.add(leftColumnLayout);

		settingsTabFactories.sort((a, b) -> a.getSettingsCaption().compareTo(b.getSettingsCaption()));
		for (ModuleSettingsPageFactory f : settingsTabFactories) {
			Anchor link = new Anchor(getPageURL(settingsPageFactory, f.getSettingsURL()), f.getSettingsCaption());
			leftColumnLayout.add(link);
		}

		return marginLayout;
	}

	@Override
	protected Component createRightColumnContent() {

		// pokud není pageFactory prázdná, pak se zobrazuje konkrétní nastavení
		if (settingsTabFactory != null)
			return settingsTabFactory.createPageIfAuthorized().getContent();

		// jinak zobraz info o nabídce
		VerticalLayout layout = new VerticalLayout();

		Span span = new Span("Zvolte položku nastavení z menu");
		layout.add(span);
		layout.setHorizontalComponentAlignment(Alignment.CENTER, span);
		layout.setSpacing(true);
		layout.setPadding(true);

		return layout;
	}

}
