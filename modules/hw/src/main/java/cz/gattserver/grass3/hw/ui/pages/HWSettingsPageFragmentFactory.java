package cz.gattserver.grass3.hw.ui.pages;

import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationResult;

import cz.gattserver.grass3.hw.HWConfiguration;
import cz.gattserver.grass3.services.ConfigurationService;
import cz.gattserver.grass3.services.FileSystemService;
import cz.gattserver.grass3.ui.components.button.SaveButton;
import cz.gattserver.grass3.ui.pages.settings.AbstractPageFragmentFactory;
import cz.gattserver.grass3.ui.util.ButtonLayout;

public class HWSettingsPageFragmentFactory extends AbstractPageFragmentFactory {

	@Autowired
	private ConfigurationService configurationService;

	@Autowired
	private FileSystemService fileSystemService;

	@Override
	public void createFragment(Div layout) {
		final HWConfiguration configuration = loadConfiguration();
		final FileSystem fs = fileSystemService.getFileSystem();

		layout.add(new H2("Nastavení evidence hw"));

		/**
		 * Kořenový adresář
		 */
		final TextField outputPathField = new TextField("Nastavení kořenového adresáře");
		outputPathField.setWidth("300px");
		outputPathField.setValue(configuration.getRootDir());
		layout.add(outputPathField);

		Binder<HWConfiguration> binder = new Binder<>();
		binder.forField(outputPathField).asRequired("Kořenový adresář je povinný").withValidator((val, c) -> {
			try {
				return Files.exists(fs.getPath(val)) ? ValidationResult.ok()
						: ValidationResult.error("Kořenový adresář musí existovat");
			} catch (InvalidPathException e) {
				return ValidationResult.error("Neplatná cesta");
			}
		}).bind(HWConfiguration::getRootDir, HWConfiguration::setRootDir);

		ButtonLayout buttonLayout = new ButtonLayout();
		layout.add(buttonLayout);

		// Save tlačítko
		Button saveButton = new SaveButton(e -> {
			configuration.setRootDir((String) outputPathField.getValue());
			storeConfiguration(configuration);
		});
		binder.addValueChangeListener(l -> saveButton.setEnabled(binder.isValid()));
		buttonLayout.add(saveButton);
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
