package cz.gattserver.grass3.articles.plugins.favlink.ui.pages.settings;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.Grid.Column;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.textfield.TextFieldVariant;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationResult;
import com.vaadin.flow.data.provider.CallbackDataProvider.CountCallback;
import com.vaadin.flow.data.provider.CallbackDataProvider.FetchCallback;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.renderer.IconRenderer;
import com.vaadin.flow.data.renderer.TextRenderer;
import com.vaadin.flow.server.StreamResource;

import cz.gattserver.common.util.HumanBytesSizeFormatter;
import cz.gattserver.grass3.articles.plugins.favlink.config.FavlinkConfiguration;
import cz.gattserver.grass3.articles.plugins.favlink.strategies.CombinedFaviconObtainStrategy;
import cz.gattserver.grass3.services.ConfigurationService;
import cz.gattserver.grass3.services.FileSystemService;
import cz.gattserver.grass3.ui.components.button.SaveButton;
import cz.gattserver.grass3.ui.pages.settings.AbstractPageFragmentFactory;
import cz.gattserver.grass3.ui.pages.template.GrassPage;
import cz.gattserver.grass3.ui.util.ButtonLayout;
import cz.gattserver.web.common.ui.window.ConfirmDialog;

public class FavlinkSettingsPageFragmentFactory extends AbstractPageFragmentFactory {

	private static final Logger logger = LoggerFactory.getLogger(FavlinkSettingsPageFragmentFactory.class);

	@Autowired
	private ConfigurationService configurationService;

	@Autowired
	private FileSystemService fileSystemService;

	private String filterName;

	@Override
	public void createFragment(Div layout) {
		final FavlinkConfiguration configuration = loadConfiguration();
		final FileSystem fs = fileSystemService.getFileSystem();

		layout.add(new H2("Nastavení favicon pluginu"));

		// Výstupní cesta
		TextField outputPathField = new TextField("Nastavení kořenového adresáře");
		outputPathField.setValue(configuration.getOutputPath());
		outputPathField.setWidth("300px");
		layout.add(outputPathField);

		Binder<FavlinkConfiguration> binder = new Binder<>();
		binder.forField(outputPathField).asRequired("Výstupní adresář je povinný").withValidator((val, c) -> {
			try {
				return Files.exists(fs.getPath(val)) ? ValidationResult.ok()
						: ValidationResult.error("Výstupní adresář musí existovat");
			} catch (InvalidPathException e) {
				return ValidationResult.error("Neplatná cesta");
			}
		}).bind(FavlinkConfiguration::getOutputPath, FavlinkConfiguration::setOutputPath);

		ButtonLayout btnLayout = new ButtonLayout();
		layout.add(btnLayout);

		// Save tlačítko
		SaveButton saveButton = new SaveButton(event -> {
			if (binder.validate().isOk()) {
				configuration.setOutputPath((String) outputPathField.getValue());
				storeConfiguration(configuration);
			}
		});
		binder.addValueChangeListener(l -> saveButton.setEnabled(binder.isValid()));
		btnLayout.add(saveButton);

		Path path = fileSystemService.getFileSystem().getPath(configuration.getOutputPath());

		if (Files.exists(path)) {
			layout.add(new H2("Přehled existujících favicon"));
			Grid<Path> grid = new Grid<>();
			grid.setWidth("100%");
			grid.setHeight("500px");
			grid.addThemeVariants(GridVariant.LUMO_COLUMN_BORDERS, GridVariant.LUMO_ROW_STRIPES,
					GridVariant.LUMO_COMPACT);

			layout.add(grid);

			grid.addColumn(new IconRenderer<Path>(p -> {
				Image img = new Image(new StreamResource(p.getFileName().toString(), () -> {
					try {
						return Files.newInputStream(p);
					} catch (IOException e) {
						logger.error("Nezdařilo se otevřít favicon " + p.getFileName().toString(), e);
					}
					return null;
				}), p.getFileName().toString());
				img.setWidth("16px");
				img.setHeight("16px");
				return img;
			}, c -> "")).setFlexGrow(0).setWidth("31px").setHeader("").setTextAlign(ColumnTextAlign.CENTER);

			Column<Path> nameColumn = grid
					.addColumn(new TextRenderer<>(
							p -> p.getFileName().toString().substring(0, p.getFileName().toString().lastIndexOf('.'))))
					.setHeader("Název").setFlexGrow(100);

			grid.addColumn(new TextRenderer<>(
					p -> p.getFileName().toString().substring(p.getFileName().toString().lastIndexOf('.'))))
					.setHeader("Typ").setWidth("40px").setFlexGrow(0);

			grid.addColumn(new ComponentRenderer<>(p -> {
				Button button = new Button("Smazat", be -> {
					new ConfirmDialog("Opravdu smazat favicon?", e -> {
						try {
							Files.delete(p);
							populateGrid(grid, path);
						} catch (IOException e1) {
							logger.error("Nezdařilo se smazat favicon " + p.getFileName().toString(), e);
						}
					}).open();
				});
				button.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);
				return button;
			})).setHeader("Smazat").setTextAlign(ColumnTextAlign.CENTER).setAutoWidth(true);

			grid.addColumn(new ComponentRenderer<>(p -> {
				Button button = new Button("Přegenerovat", be -> {
					new ConfirmDialog("Opravdu přegenerovat favicon?", e -> {
						try {
							Files.delete(p);
							String fileName = p.getFileName().toString();
							String urlName = "http://" + fileName.substring(0, fileName.lastIndexOf('.'));
							new CombinedFaviconObtainStrategy().obtainFaviconURL(urlName, GrassPage.getContextPath());
							populateGrid(grid, path);
						} catch (IOException e1) {
							logger.error("Nezdařilo se smazat favicon " + p.getFileName().toString(), e);
						}
					}).open();
				});
				button.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);
				return button;
			})).setHeader("Přegenerovat").setTextAlign(ColumnTextAlign.CENTER).setAutoWidth(true);

			grid.addColumn(new TextRenderer<>(p -> formatSize(p))).setHeader("Velikost")
					.setTextAlign(ColumnTextAlign.END).setFlexGrow(0).setWidth("60px");

			HeaderRow filteringHeader = grid.appendHeaderRow();

			// Obsah
			TextField contentFilterField = new TextField();
			contentFilterField.addThemeVariants(TextFieldVariant.LUMO_SMALL);
			contentFilterField.setWidth("100%");
			contentFilterField.addValueChangeListener(e -> {
				filterName = e.getValue();
				populateGrid(grid, path);
			});
			filteringHeader.getCell(nameColumn).setComponent(contentFilterField);

			populateGrid(grid, path);
		}
	}

	private Stream<Path> createStream(Path path) {
		try {
			// zde se úmyslně nezavírá stream, protože se předává dál do vaadin
			return Files.list(path)
					.filter(p -> p.getFileName().toString().substring(0, p.getFileName().toString().lastIndexOf('.'))
							.contains(filterName == null ? "" : filterName));
		} catch (IOException e) {
			logger.error("Nezdařilo se načíst favicon sobory z " + path.getFileName().toString(), e);
		}
		return new ArrayList<Path>().stream();
	}

	private long count(Path path) {
		try (Stream<Path> stream = Files.list(path)) {
			return stream.filter(p -> p.getFileName().toString().contains(filterName == null ? "" : filterName))
					.count();
		} catch (IOException e) {
			logger.error("Nezdařilo se načíst galerie z " + path.getFileName().toString(), e);
			return 0;
		}
	}

	private void populateGrid(Grid<Path> grid, Path path) {
		FetchCallback<Path, Void> fetchCallback = q -> createStream(path).skip(q.getOffset()).limit(q.getLimit());
		CountCallback<Path, Void> countCallback = q -> (int) count(path);
		grid.setDataProvider(DataProvider.fromCallbacks(fetchCallback, countCallback));
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
