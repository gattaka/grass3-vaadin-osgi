package cz.gattserver.grass3.campgames.ui.pages;

import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.data.Binder;
import com.vaadin.data.ValidationResult;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

import cz.gattserver.grass3.campgames.CampgamesConfiguration;
import cz.gattserver.grass3.server.GrassRequest;
import cz.gattserver.grass3.services.ConfigurationService;
import cz.gattserver.grass3.services.FileSystemService;
import cz.gattserver.grass3.ui.pages.settings.AbstractSettingsPage;
import cz.gattserver.web.common.ui.H2Label;

public class CampgamesSettingsPage extends AbstractSettingsPage {

	@Autowired
	private ConfigurationService configurationService;

	@Autowired
	private FileSystemService fileSystemService;

	public CampgamesSettingsPage(GrassRequest request) {
		super(request);
	}

	@Override
	protected Component createContent() {
		final CampgamesConfiguration configuration = loadConfiguration();
		final FileSystem fs = fileSystemService.getFileSystem();

		VerticalLayout layout = new VerticalLayout();

		layout.setMargin(true);
		layout.setSpacing(true);

		VerticalLayout settingsLayout = new VerticalLayout();
		layout.addComponent(settingsLayout);

		settingsLayout.removeAllComponents();
		settingsLayout.addComponent(new H2Label("Nastavení evidence táborových her"));

		// Nadpis zůstane odsazen a jednotlivá pole se můžou mezi sebou rozsázet
		VerticalLayout settingsFieldsLayout = new VerticalLayout();
		settingsFieldsLayout.setSpacing(true);
		settingsFieldsLayout.setMargin(false);
		settingsLayout.addComponent(settingsFieldsLayout);
		settingsFieldsLayout.setSizeFull();

		/**
		 * Kořenový adresář
		 */
		final TextField outputPathField = new TextField("Nastavení kořenového adresáře");
		outputPathField.setValue(configuration.getRootDir());
		outputPathField.setWidth("300px");
		settingsFieldsLayout.addComponent(outputPathField);

		Binder<CampgamesConfiguration> binder = new Binder<>();
		binder.forField(outputPathField).asRequired("Kořenový adresář je povinný").withValidator((val, c) -> {
			try {
				return Files.exists(fs.getPath(val)) ? ValidationResult.ok()
						: ValidationResult.error("Kořenový adresář musí existovat");
			} catch (InvalidPathException e) {
				return ValidationResult.error("Neplatná cesta");
			}
		}).bind(CampgamesConfiguration::getRootDir, CampgamesConfiguration::setRootDir);

		// Save tlačítko
		Button saveButton = new Button("Uložit", e -> {
			configuration.setRootDir((String) outputPathField.getValue());
			storeConfiguration(configuration);
		});
		binder.addValueChangeListener(l -> saveButton.setEnabled(binder.isValid()));

		settingsFieldsLayout.addComponent(saveButton);

		return layout;

	}

	private CampgamesConfiguration loadConfiguration() {
		CampgamesConfiguration configuration = new CampgamesConfiguration();
		configurationService.loadConfiguration(configuration);
		return configuration;
	}

	private void storeConfiguration(CampgamesConfiguration configuration) {
		configurationService.saveConfiguration(configuration);
	}

}
