package cz.gattserver.grass3.pg.ui.pages;

import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.data.Binder;
import com.vaadin.data.ValidationResult;
import com.vaadin.data.validator.StringLengthValidator;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

import cz.gattserver.grass3.pg.config.PGConfiguration;
import cz.gattserver.grass3.pg.service.PGService;
import cz.gattserver.grass3.server.GrassRequest;
import cz.gattserver.grass3.services.FileSystemService;
import cz.gattserver.grass3.ui.pages.settings.AbstractSettingsPage;
import cz.gattserver.web.common.ui.H2Label;

public class PGSettingsPage extends AbstractSettingsPage {

	@Autowired
	private PGService pgService;

	@Autowired
	private FileSystemService fileSystemService;

	public PGSettingsPage(GrassRequest request) {
		super(request);
	}

	@Override
	protected Component createContent() {
		final PGConfiguration configuration = pgService.loadConfiguration();
		final FileSystem fs = fileSystemService.getFileSystem();

		VerticalLayout layout = new VerticalLayout();

		layout.setMargin(true);
		layout.setSpacing(true);

		VerticalLayout settingsLayout = new VerticalLayout();
		layout.addComponent(settingsLayout);

		settingsLayout.removeAllComponents();
		settingsLayout.addComponent(new H2Label("Nastavení fotogalerie"));

		// Nadpis zůstane odsazen a jednotlivá pole se můžou mezi sebou rozsázet
		VerticalLayout settingsFieldsLayout = new VerticalLayout();
		settingsLayout.addComponent(settingsFieldsLayout);
		settingsFieldsLayout.setSpacing(true);
		settingsFieldsLayout.setMargin(false);
		settingsFieldsLayout.setSizeFull();

		Binder<PGConfiguration> binder = new Binder<>();

		// Název adresářů miniatur
		final TextField miniaturesDirField = new TextField("Název adresářů miniatur");
		miniaturesDirField.setValue(String.valueOf(configuration.getMiniaturesDir()));
		miniaturesDirField.setWidth("300px");
		settingsFieldsLayout.addComponent(miniaturesDirField);

		binder.forField(miniaturesDirField).asRequired("Nesmí být prázdné")
				.withValidator(new StringLengthValidator("Neodpovídá povolené délce", 1, 1024))
				.bind(PGConfiguration::getMiniaturesDir, PGConfiguration::setMiniaturesDir);

		// Kořenový adresář fotogalerií
		final TextField rootDirField = new TextField("Kořenový adresář fotogalerií");
		rootDirField.setValue(String.valueOf(configuration.getRootDir()));
		rootDirField.setWidth("300px");
		settingsFieldsLayout.addComponent(rootDirField);

		binder.forField(rootDirField).asRequired("Kořenový adresář je povinný").withValidator((val, c) -> {
			try {
				return Files.exists(fs.getPath(val)) ? ValidationResult.ok()
						: ValidationResult.error("Kořenový adresář musí existovat");
			} catch (InvalidPathException e) {
				return ValidationResult.error("Neplatná cesta");
			}
		}).bind(PGConfiguration::getRootDir, PGConfiguration::setRootDir);

		// Save tlačítko
		Button saveButton = new Button("Uložit", event -> {
			if (rootDirField.getComponentError() == null && miniaturesDirField.getComponentError() == null) {
				configuration.setRootDir(rootDirField.getValue());
				configuration.setMiniaturesDir(miniaturesDirField.getValue());
				pgService.storeConfiguration(configuration);
			}
		});
		binder.addValueChangeListener(l -> saveButton.setEnabled(binder.isValid()));
		settingsFieldsLayout.addComponent(saveButton);

		return layout;
	}

}
