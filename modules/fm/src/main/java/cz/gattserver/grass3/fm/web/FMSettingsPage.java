package cz.gattserver.grass3.fm.web;

import java.nio.file.FileSystem;
import java.nio.file.Files;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.data.Binder;
import com.vaadin.data.ValidationResult;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

import cz.gattserver.grass3.fm.config.FMConfiguration;
import cz.gattserver.grass3.server.GrassRequest;
import cz.gattserver.grass3.services.ConfigurationService;
import cz.gattserver.grass3.services.FileSystemService;
import cz.gattserver.grass3.ui.pages.settings.AbstractSettingsPage;
import cz.gattserver.web.common.ui.H2Label;

public class FMSettingsPage extends AbstractSettingsPage {

	@Autowired
	private ConfigurationService configurationService;

	@Autowired
	private FileSystemService fileSystemService;

	public FMSettingsPage(GrassRequest request) {
		super(request);
	}

	@Override
	protected Component createContent() {

		final FMConfiguration configuration = loadConfiguration();
		final FileSystem fs = fileSystemService.getFileSystem();

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

		// Kořenový adresář
		final TextField outputPathField = new TextField("Nastavení kořenového adresáře");
		outputPathField.setValue(configuration.getRootDir());
		settingsFieldsLayout.addComponent(outputPathField);

		Binder<FMConfiguration> binder = new Binder<>();
		binder.forField(outputPathField).asRequired("Kořenový adresář je povinný")
				.withValidator((val, c) -> Files.exists(fs.getPath(val)) ? ValidationResult.ok()
						: ValidationResult.error("Kořenový adresář musí existovat"))
				.bind(FMConfiguration::getRootDir, FMConfiguration::setRootDir);

		// Save tlačítko
		Button saveButton = new Button("Uložit", e -> {
			configuration.setRootDir((String) outputPathField.getValue());
			storeConfiguration(configuration);
		});
		binder.addValueChangeListener(l -> saveButton.setEnabled(binder.isValid()));

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