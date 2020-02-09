package cz.gattserver.grass3.print3d.ui.pages;

import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationResult;

import cz.gattserver.grass3.print3d.config.Print3dConfiguration;
import cz.gattserver.grass3.print3d.service.Print3dService;
import cz.gattserver.grass3.services.FileSystemService;
import cz.gattserver.grass3.ui.components.button.SaveButton;
import cz.gattserver.grass3.ui.pages.factories.template.PageFactory;
import cz.gattserver.grass3.ui.pages.settings.AbstractPageFragmentFactory;
import cz.gattserver.grass3.ui.util.ButtonLayout;
import cz.gattserver.web.common.ui.Breakline;

public class Print3dSettingsPageFragmentFactory extends AbstractPageFragmentFactory {

	@Autowired
	private Print3dService pgService;

	@Autowired
	private FileSystemService fileSystemService;

	@Resource(name = "print3dViewerPageFactory")
	private PageFactory print3dViewerPageFactory;

	@Override
	public void createFragment(Div layout) {
		final Print3dConfiguration configuration = pgService.loadConfiguration();
		final FileSystem fs = fileSystemService.getFileSystem();

		layout.add(new H2("Nastavení knihovny 3D modelů"));

		Binder<Print3dConfiguration> binder = new Binder<>();

		// Kořenový adresář projektů
		final TextField rootDirField = new TextField("Kořenový adresář 3d projektů");
		rootDirField.setValue(String.valueOf(configuration.getRootDir()));
		rootDirField.setWidth("300px");
		layout.add(rootDirField);

		layout.add(new Breakline());

		binder.forField(rootDirField).asRequired("Kořenový adresář je povinný").withValidator((val, c) -> {
			try {
				return Files.exists(fs.getPath(val)) ? ValidationResult.ok()
						: ValidationResult.error("Kořenový adresář musí existovat");
			} catch (InvalidPathException e) {
				return ValidationResult.error("Neplatná cesta");
			}
		}).bind(Print3dConfiguration::getRootDir, Print3dConfiguration::setRootDir);

		// Save tlačítko
		ButtonLayout btnLayout = new ButtonLayout();
		SaveButton saveButton = new SaveButton(event -> {
			if (binder.validate().isOk()) {
				configuration.setRootDir(rootDirField.getValue());
				pgService.storeConfiguration(configuration);
				UI.getCurrent().getPage().reload();
			}
		});
		binder.addValueChangeListener(l -> saveButton.setEnabled(binder.isValid()));
		btnLayout.add(saveButton);
		layout.add(btnLayout);
	}

}
