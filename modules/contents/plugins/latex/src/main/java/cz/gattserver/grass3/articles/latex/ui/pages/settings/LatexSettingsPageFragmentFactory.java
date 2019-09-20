package cz.gattserver.grass3.articles.latex.ui.pages.settings;

import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationResult;

import cz.gattserver.grass3.articles.latex.config.LatexConfiguration;
import cz.gattserver.grass3.services.ConfigurationService;
import cz.gattserver.grass3.services.FileSystemService;
import cz.gattserver.grass3.ui.pages.settings.AbstractPageFragmentFactory;

public class LatexSettingsPageFragmentFactory extends AbstractPageFragmentFactory {

	@Autowired
	private ConfigurationService configurationService;

	@Autowired
	private FileSystemService fileSystemService;

	@Override
	public void createFragment(Div layout) {
		final LatexConfiguration configuration = loadConfiguration();
		final FileSystem fs = fileSystemService.getFileSystem();

		VerticalLayout settingsLayout = new VerticalLayout();
		layout.add(settingsLayout);

		settingsLayout.add(new H2("Nastavení latex pluginu"));

		/**
		 * Výstupní cesta
		 */
		final TextField outputPathField = new TextField("Nastavení výstupního adresáře");
		outputPathField.setValue(configuration.getOutputPath());
		outputPathField.setWidth("300px");
		layout.add(outputPathField);

		Binder<LatexConfiguration> binder = new Binder<>();
		binder.forField(outputPathField).asRequired("Výstupní adresář je povinný").withValidator((val, c) -> {
			try {
				return Files.exists(fs.getPath(val)) ? ValidationResult.ok()
						: ValidationResult.error("Výstupní adresář musí existovat");
			} catch (InvalidPathException e) {
				return ValidationResult.error("Neplatná cesta");
			}
		}).bind(LatexConfiguration::getOutputPath, LatexConfiguration::setOutputPath);

		/**
		 * Save tlačítko
		 */
		Button saveButton = new Button("Uložit", e -> {
			configuration.setOutputPath((String) outputPathField.getValue());
			storeConfiguration(configuration);
		});
		layout.add(saveButton);
		binder.addValueChangeListener(l -> saveButton.setEnabled(binder.isValid()));
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
