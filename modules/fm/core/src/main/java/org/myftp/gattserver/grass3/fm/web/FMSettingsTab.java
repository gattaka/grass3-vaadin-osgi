package org.myftp.gattserver.grass3.fm.web;

import javax.annotation.Resource;
import org.myftp.gattserver.grass3.config.IConfigurationService;
import org.myftp.gattserver.grass3.fm.config.FMConfiguration;
import org.myftp.gattserver.grass3.tabs.template.AbstractSettingsTab;
import org.myftp.gattserver.grass3.util.GrassRequest;
import org.springframework.context.annotation.Scope;

import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Button.ClickEvent;

@org.springframework.stereotype.Component("fmSettingsTab")
@Scope("prototype")
public class FMSettingsTab extends AbstractSettingsTab {

	private static final long serialVersionUID = -3310643769376755875L;

	@Resource(name = "configurationService")
	IConfigurationService configurationService;

	public FMSettingsTab(GrassRequest request) {
		super(request);
	}

	@Override
	protected Component createContent() {

		final FMConfiguration configuration = loadConfiguration();

		VerticalLayout layout = new VerticalLayout();

		layout.setMargin(true);
		layout.setSpacing(true);

		VerticalLayout settingsLayout = new VerticalLayout();
		layout.addComponent(settingsLayout);

		settingsLayout.removeAllComponents();
		settingsLayout.addComponent(new Label("<h2>Nastavení</h2>",
				ContentMode.HTML));

		// Nadpis zůstane odsazen a jednotlivá pole se můžou mezi sebou rozsázet
		VerticalLayout settingsFieldsLayout = new VerticalLayout();
		settingsLayout.addComponent(settingsFieldsLayout);
		settingsFieldsLayout.setSpacing(true);
		settingsFieldsLayout.setSizeFull();

		/**
		 * Kořenový adresář
		 */
		final TextField outputPathField = new TextField(
				"Nastavení kořenového adresáře");
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

	private FMConfiguration loadConfiguration() {
		FMConfiguration configuration = new FMConfiguration();
		configurationService.loadConfiguration(configuration);
		return configuration;
	}

	private void storeConfiguration(FMConfiguration configuration) {
		configurationService.saveConfiguration(configuration);
	}

}