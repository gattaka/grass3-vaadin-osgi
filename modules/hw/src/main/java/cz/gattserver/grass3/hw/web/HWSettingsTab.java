package cz.gattserver.grass3.hw.web;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Button.ClickEvent;

import cz.gattserver.grass3.config.ConfigurationService;
import cz.gattserver.grass3.hw.HWConfiguration;
import cz.gattserver.grass3.tabs.template.AbstractSettingsTab;
import cz.gattserver.grass3.ui.util.GrassRequest;
import cz.gattserver.web.common.ui.H2Label;

public class HWSettingsTab extends AbstractSettingsTab {

	private static final long serialVersionUID = -3310643769376755875L;

	@Autowired
	private ConfigurationService configurationService;

	public HWSettingsTab(GrassRequest request) {
		super(request);
	}

	@Override
	protected Component createContent() {

		final HWConfiguration configuration = loadConfiguration();

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
		settingsFieldsLayout.setSpacing(true);
		settingsFieldsLayout.setSizeFull();

		/**
		 * Kořenový adresář
		 */
		final TextField outputPathField = new TextField("Nastavení kořenového adresáře");
		outputPathField.setValue(configuration.getRootDir());
		settingsFieldsLayout.addComponent(outputPathField);

		/**
		 * Save tlačítko
		 */
		Button saveButton = new Button("Uložit", new Button.ClickListener() {

			private static final long serialVersionUID = 8490964871266821307L;

			public void buttonClick(ClickEvent event) {
				configuration.setRootDir((String) outputPathField.getValue());
				storeConfiguration(configuration);
			}
		});

		settingsFieldsLayout.addComponent(saveButton);

		return layout;

	}

	private HWConfiguration loadConfiguration() {
		HWConfiguration configuration = new HWConfiguration();
		configurationService.loadConfiguration(configuration);
		return configuration;
	}

	private void storeConfiguration(HWConfiguration configuration) {
		configurationService.saveConfiguration(configuration);
	}

}
