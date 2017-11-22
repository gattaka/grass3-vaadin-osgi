package cz.gattserver.grass3.articles.favlink.web;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

import cz.gattserver.grass3.articles.favlink.config.FavlinkConfiguration;
import cz.gattserver.grass3.server.GrassRequest;
import cz.gattserver.grass3.services.ConfigurationService;
import cz.gattserver.grass3.ui.pages.settings.ModuleSettingsPage;
import cz.gattserver.web.common.ui.H2Label;

public class FavlinkSettingsPage extends ModuleSettingsPage {

	@Autowired
	private ConfigurationService configurationService;

	public FavlinkSettingsPage(GrassRequest request) {
		super(request);
	}

	@Override
	protected Component createContent() {
		final FavlinkConfiguration configuration = loadConfiguration();

		VerticalLayout layout = new VerticalLayout();

		layout.setMargin(true);
		layout.setSpacing(true);

		VerticalLayout settingsLayout = new VerticalLayout();
		layout.addComponent(settingsLayout);

		settingsLayout.removeAllComponents();
		settingsLayout.addComponent(new H2Label("Nastavení"));

		// Nadpis zůstane odsazen a jednotlivá pole se můžou mezi sebou rozsázet
		VerticalLayout settingsFieldsLayout = new VerticalLayout();
		settingsLayout.addComponent(settingsFieldsLayout);
		settingsFieldsLayout.setMargin(false);
		settingsFieldsLayout.setSpacing(true);
		settingsFieldsLayout.setSizeFull();

		/**
		 * Výstupní cesta
		 */
		final TextField outputPathField = new TextField("Nastavení výstupní cesty");
		outputPathField.setValue(configuration.getOutputPath());
		settingsFieldsLayout.addComponent(outputPathField);

		/**
		 * Save tlačítko
		 */

		Button saveButton = new Button("Uložit", event -> {
			configuration.setOutputPath((String) outputPathField.getValue());
			storeConfiguration(configuration);
		});

		settingsFieldsLayout.addComponent(saveButton);

		return layout;
	}

	private FavlinkConfiguration loadConfiguration() {
		FavlinkConfiguration configuration = new FavlinkConfiguration();
		configurationService.loadConfiguration(configuration);
		return configuration;
	}

	private void storeConfiguration(FavlinkConfiguration configuration) {
		configurationService.saveConfiguration(configuration);
	}

}
