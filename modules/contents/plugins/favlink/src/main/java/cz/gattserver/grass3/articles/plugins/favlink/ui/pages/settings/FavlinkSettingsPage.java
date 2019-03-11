package cz.gattserver.grass3.articles.plugins.favlink.ui.pages.settings;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.data.Binder;
import com.vaadin.data.ValidationResult;
import com.vaadin.server.StreamResource;
import com.vaadin.server.StreamResource.StreamSource;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.components.grid.HeaderRow;
import com.vaadin.ui.Component;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.Column;
import com.vaadin.ui.Image;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.renderers.ComponentRenderer;
import com.vaadin.ui.themes.ValoTheme;

import cz.gattserver.common.util.HumanBytesSizeFormatter;
import cz.gattserver.grass3.articles.plugins.favlink.config.FavlinkConfiguration;
import cz.gattserver.grass3.server.GrassRequest;
import cz.gattserver.grass3.services.ConfigurationService;
import cz.gattserver.grass3.services.FileSystemService;
import cz.gattserver.grass3.ui.pages.settings.AbstractSettingsPage;
import cz.gattserver.grass3.ui.util.GridUtils;
import cz.gattserver.web.common.ui.H2Label;
import cz.gattserver.web.common.ui.window.ConfirmWindow;

public class FavlinkSettingsPage extends AbstractSettingsPage {

	private static final Logger logger = LoggerFactory.getLogger(FavlinkSettingsPage.class);

	@Autowired
	private ConfigurationService configurationService;

	@Autowired
	private FileSystemService fileSystemService;

	private String filterName;

	public FavlinkSettingsPage(GrassRequest request) {
		super(request);
	}

	@Override
	protected Component createContent() {
		final FavlinkConfiguration configuration = loadConfiguration();
		final FileSystem fs = fileSystemService.getFileSystem();

		VerticalLayout layout = new VerticalLayout();

		layout.setMargin(true);
		layout.setSpacing(true);

		VerticalLayout settingsLayout = new VerticalLayout();
		layout.addComponent(settingsLayout);

		settingsLayout.removeAllComponents();
		settingsLayout.addComponent(new H2Label("Nastavení favlink pluginu"));

		// Nadpis zůstane odsazen a jednotlivá pole se můžou mezi sebou rozsázet
		VerticalLayout settingsFieldsLayout = new VerticalLayout();
		settingsLayout.addComponent(settingsFieldsLayout);
		settingsFieldsLayout.setMargin(false);
		settingsFieldsLayout.setSpacing(true);
		settingsFieldsLayout.setSizeFull();

		// Výstupní cesta
		TextField outputPathField = new TextField("Nastavení výstupního adresáře");
		outputPathField.setValue(configuration.getOutputPath());
		outputPathField.setWidth("300px");
		settingsFieldsLayout.addComponent(outputPathField);

		Binder<FavlinkConfiguration> binder = new Binder<>();
		binder.forField(outputPathField).asRequired("Výstupní adresář je povinný").withValidator((val, c) -> {
			try {
				return Files.exists(fs.getPath(val)) ? ValidationResult.ok()
						: ValidationResult.error("Výstupní adresář musí existovat");
			} catch (InvalidPathException e) {
				return ValidationResult.error("Neplatná cesta");
			}
		}).bind(FavlinkConfiguration::getOutputPath, FavlinkConfiguration::setOutputPath);

		// Save tlačítko
		Button saveButton = new Button("Uložit", event -> {
			configuration.setOutputPath((String) outputPathField.getValue());
			storeConfiguration(configuration);
		});
		binder.addValueChangeListener(l -> saveButton.setEnabled(binder.isValid()));

		settingsFieldsLayout.addComponent(saveButton);

		Path path = fileSystemService.getFileSystem().getPath(configuration.getOutputPath());

		if (Files.exists(path)) {
			Grid<Path> grid = new Grid<>("Přehled existujících favicon");
			grid.setWidth("100%");
			grid.setHeight("500px");

			settingsFieldsLayout.addComponent(grid);

			grid.addColumn(p -> {
				Image img = new Image(null, new StreamResource(new StreamSource() {
					private static final long serialVersionUID = 8676489834467009848L;

					@Override
					public InputStream getStream() {
						try {
							return Files.newInputStream(p);
						} catch (IOException e) {
							logger.error("Nezdařilo se otevřít favicon " + p.getFileName().toString(), e);
						}
						return null;
					}
				}, p.getFileName().toString()));
				img.setHeight("16px");
				img.setWidth("16px");
				return img;
			}, new ComponentRenderer()).setWidth(GridUtils.ICON_COLUMN_WIDTH).setCaption("");

			Column<Path, String> nameColumn = grid
					.addColumn(
							p -> p.getFileName().toString().substring(0, p.getFileName().toString().lastIndexOf('.')))
					.setCaption("Název").setExpandRatio(1);

			grid.addColumn(p -> p.getFileName().toString().substring(p.getFileName().toString().lastIndexOf('.')))
					.setCaption("Typ");

			grid.addColumn(p -> {
				Button button = new Button("Smazat", new Button.ClickListener() {
					private static final long serialVersionUID = 1996102817811495323L;

					@Override
					public void buttonClick(ClickEvent event) {
						UI.getCurrent().addWindow(new ConfirmWindow("Opravdu smazat favicon?", e -> {
							try {
								Files.delete(p);
								populateGrid(grid, path);
							} catch (IOException e1) {
								logger.error("Nezdařilo se smazat favicon " + p.getFileName().toString(), e);
							}
						}));
					}
				});
				button.setStyleName(ValoTheme.BUTTON_LINK);
				return button;
			}).setRenderer(new ComponentRenderer()).setCaption("Smazat");

			grid.addColumn(p -> formatSize(p)).setCaption("Velikost").setStyleGenerator(item -> "v-align-right");

			HeaderRow filteringHeader = grid.appendHeaderRow();

			// Obsah
			TextField contentFilterField = new TextField();
			contentFilterField.addStyleName(ValoTheme.TEXTFIELD_TINY);
			contentFilterField.setWidth("100%");
			contentFilterField.addValueChangeListener(e -> {
				filterName = e.getValue();
				populateGrid(grid, path);
			});
			filteringHeader.getCell(nameColumn).setComponent(contentFilterField);

			populateGrid(grid, path);
		}

		return layout;
	}

	private Stream<Path> createStream(Path path) {
		try {
			return Files.list(path)
					.filter(p -> p.getFileName().toString().substring(0, p.getFileName().toString().lastIndexOf('.'))
							.contains(filterName == null ? "" : filterName));
		} catch (IOException e) {
			logger.error("Nezdařilo se načíst favicon sobory z " + path.getFileName().toString(), e);
		}
		return new ArrayList<Path>().stream();
	}

	private void populateGrid(Grid<Path> grid, Path path) {
		grid.setDataProvider((sortOrder, offset, limit) -> createStream(path).skip(offset).limit(limit),
				() -> (int) createStream(path).count());
	}

	private String formatSize(Path path) {
		try {
			return HumanBytesSizeFormatter.format(Files.size(path));
		} catch (IOException e) {
			logger.error("Nezdařilo se zjistit velikost souboru " + path.getFileName().toString(), e);
		}
		return "";
	}

	private FavlinkConfiguration loadConfiguration() {
		FavlinkConfiguration configuration = new FavlinkConfiguration();
		configurationService.loadConfiguration(configuration);
		return configuration;
	}

	private void storeConfiguration(FavlinkConfiguration configuration) {
		configurationService.saveConfiguration(configuration);
	}

}
