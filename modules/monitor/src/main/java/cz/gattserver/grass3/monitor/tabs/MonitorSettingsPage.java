package cz.gattserver.grass3.monitor.tabs;

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

import cz.gattserver.grass3.monitor.config.MonitorConfiguration;
import cz.gattserver.grass3.monitor.facade.MonitorFacade;
import cz.gattserver.grass3.server.GrassRequest;
import cz.gattserver.grass3.services.FileSystemService;
import cz.gattserver.grass3.ui.pages.settings.AbstractSettingsPage;
import cz.gattserver.web.common.ui.H2Label;

public class MonitorSettingsPage extends AbstractSettingsPage {

	@Autowired
	private MonitorFacade monitorFacade;

	@Autowired
	private FileSystemService fileSystemService;

	public MonitorSettingsPage(GrassRequest request) {
		super(request);
	}

	@Override
	protected Component createContent() {
		final MonitorConfiguration configuration = monitorFacade.getConfiguration();
		final FileSystem fs = fileSystemService.getFileSystem();

		VerticalLayout layout = new VerticalLayout();

		layout.setMargin(true);
		layout.setSpacing(true);

		VerticalLayout settingsLayout = new VerticalLayout();
		layout.addComponent(settingsLayout);

		settingsLayout.removeAllComponents();
		settingsLayout.addComponent(new H2Label("Nastavení system monitoru"));

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
		scriptsDirField.setWidth("300px");
		scriptsDirField.setValue(String.valueOf(configuration.getScriptsDir()));
		settingsFieldsLayout.addComponent(scriptsDirField);

		Binder<MonitorConfiguration> binder = new Binder<>();
		binder.forField(scriptsDirField).asRequired("Adresář skriptů je povinný").withValidator((val, c) -> {
			try {
				return Files.exists(fs.getPath(val)) ? ValidationResult.ok()
						: ValidationResult.error("Adresář skriptů musí existovat");
			} catch (InvalidPathException e) {
				return ValidationResult.error("Neplatná cesta");
			}
		}).bind(MonitorConfiguration::getScriptsDir, MonitorConfiguration::setScriptsDir);

		/**
		 * Save tlačítko
		 */
		Button saveButton = new Button("Uložit", event -> {
			if (scriptsDirField.getComponentError() == null) {
				configuration.setScriptsDir(scriptsDirField.getValue());
				monitorFacade.storeConfiguration(configuration);
			}
		});
		binder.addValueChangeListener(l -> saveButton.setEnabled(binder.isValid()));

		settingsFieldsLayout.addComponent(saveButton);

		return layout;
	}

}
