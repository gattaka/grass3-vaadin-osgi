package cz.gattserver.grass3.articles.latex.web;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Button.ClickEvent;

import cz.gattserver.grass3.articles.latex.config.LatexConfiguration;
import cz.gattserver.grass3.config.ConfigurationService;
import cz.gattserver.grass3.tabs.template.AbstractSettingsTab;
import cz.gattserver.grass3.ui.util.GrassRequest;

public class LatexSettingsTab extends AbstractSettingsTab {

	private static final long serialVersionUID = -3310643769376755875L;

	@Autowired
	private ConfigurationService configurationService;

	public LatexSettingsTab(GrassRequest request) {
		super(request);
	}

	@Override
	protected Component createContent() {

		final LatexConfiguration configuration = loadConfiguration();

		VerticalLayout layout = new VerticalLayout();

		layout.setMargin(true);
		layout.setSpacing(true);

		VerticalLayout settingsLayout = new VerticalLayout();
		layout.addComponent(settingsLayout);

		settingsLayout.removeAllComponents();
		settingsLayout.addComponent(new Label("<h2>Nastavení</h2>", ContentMode.HTML));

		// Nadpis zůstane odsazen a jednotlivá pole se můžou mezi sebou rozsázet
		VerticalLayout settingsFieldsLayout = new VerticalLayout();
		settingsLayout.addComponent(settingsFieldsLayout);
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

		Button saveButton = new Button("Uložit", new Button.ClickListener() {

			private static final long serialVersionUID = 8490964871266821307L;

			public void buttonClick(ClickEvent event) {
				configuration.setOutputPath((String) outputPathField.getValue());
				storeConfiguration(configuration);
			}
		});

		settingsFieldsLayout.addComponent(saveButton);

		return layout;

	}

	private LatexConfiguration loadConfiguration() {
		LatexConfiguration configuration = new LatexConfiguration();
		configurationService.loadConfiguration(configuration);
		return configuration;
	}

	private void storeConfiguration(LatexConfiguration configuration) {
		configurationService.saveConfiguration(configuration);
	}

}
