package cz.gattserver.grass3.monitor.tabs;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.data.validator.StringLengthValidator;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

import cz.gattserver.grass3.monitor.config.MonitorConfiguration;
import cz.gattserver.grass3.monitor.facade.MonitorFacade;
import cz.gattserver.grass3.server.GrassRequest;
import cz.gattserver.grass3.ui.pages.settings.AbstractSettingsPage;
import cz.gattserver.web.common.ui.FieldUtils;
import cz.gattserver.web.common.ui.H2Label;

public class MonitorSettingsPage extends AbstractSettingsPage {

	@Autowired
	private MonitorFacade monitorFacade;

	public MonitorSettingsPage(GrassRequest request) {
		super(request);
	}

	@Override
	protected Component createContent() {
		VerticalLayout layout = new VerticalLayout();

		layout.setMargin(true);
		layout.setSpacing(true);

		VerticalLayout settingsLayout = new VerticalLayout();
		layout.addComponent(settingsLayout);

		final MonitorConfiguration configuration = monitorFacade.getConfiguration();

		settingsLayout.removeAllComponents();
		settingsLayout.addComponent(new H2Label("Nastavení"));

		// Nadpis zůstane odsazen a jednotlivá pole se můžou mezi sebou rozsázet
		VerticalLayout settingsFieldsLayout = new VerticalLayout();
		settingsLayout.addComponent(settingsFieldsLayout);
		settingsFieldsLayout.setMargin(false);
		settingsFieldsLayout.setSpacing(true);
		settingsFieldsLayout.setSizeFull();

		/**
		 * Adresář skriptů
		 */
		final TextField scriptsDirField = new TextField("Adresář skriptů");
		scriptsDirField.setValue(String.valueOf(configuration.getScriptsDir()));
		FieldUtils.addValidator(scriptsDirField, new StringLengthValidator("Nesmí být prázdné", 1, 1024));
		settingsFieldsLayout.addComponent(scriptsDirField);

		/**
		 * Save tlačítko
		 */
		Button saveButton = new Button("Uložit", event -> {
			if (scriptsDirField.getComponentError() == null) {
				configuration.setScriptsDir(scriptsDirField.getValue());
				monitorFacade.storeConfiguration(configuration);
			}
		});
		settingsFieldsLayout.addComponent(saveButton);

		return layout;
	}

}
