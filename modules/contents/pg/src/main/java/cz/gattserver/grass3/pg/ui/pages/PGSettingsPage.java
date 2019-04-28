package cz.gattserver.grass3.pg.ui.pages;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.UUID;
import java.util.stream.Stream;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.data.Binder;
import com.vaadin.data.ValidationResult;
import com.vaadin.data.validator.StringLengthValidator;
import com.vaadin.server.Page;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Component;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.Column;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.components.grid.HeaderRow;
import com.vaadin.ui.renderers.ComponentRenderer;
import com.vaadin.ui.themes.ValoTheme;

import cz.gattserver.common.util.HumanBytesSizeFormatter;
import cz.gattserver.grass3.events.EventBus;
import cz.gattserver.grass3.pg.config.PGConfiguration;
import cz.gattserver.grass3.pg.events.impl.PGProcessProgressEvent;
import cz.gattserver.grass3.pg.events.impl.PGProcessResultEvent;
import cz.gattserver.grass3.pg.events.impl.PGProcessStartEvent;
import cz.gattserver.grass3.pg.interfaces.PGSettingsItemTO;
import cz.gattserver.grass3.pg.interfaces.PhotogalleryPayloadTO;
import cz.gattserver.grass3.pg.interfaces.PhotogalleryRESTOverviewTO;
import cz.gattserver.grass3.pg.interfaces.PhotogalleryTO;
import cz.gattserver.grass3.pg.service.PGService;
import cz.gattserver.grass3.server.GrassRequest;
import cz.gattserver.grass3.services.FileSystemService;
import cz.gattserver.grass3.ui.pages.factories.template.PageFactory;
import cz.gattserver.grass3.ui.pages.settings.AbstractSettingsPage;
import cz.gattserver.grass3.ui.windows.ProgressWindow;
import cz.gattserver.web.common.server.URLIdentifierUtils;
import cz.gattserver.web.common.ui.H2Label;
import cz.gattserver.web.common.ui.window.ConfirmWindow;
import cz.gattserver.web.common.ui.window.InfoWindow;
import cz.gattserver.web.common.ui.window.WarnWindow;
import net.engio.mbassy.listener.Handler;

public class PGSettingsPage extends AbstractSettingsPage {

	private static final Logger logger = LoggerFactory.getLogger(PGSettingsPage.class);

	@Autowired
	private PGService pgService;

	@Autowired
	private EventBus eventBus;

	@Autowired
	private FileSystemService fileSystemService;

	@Resource(name = "pgViewerPageFactory")
	private PageFactory photogalleryViewerPageFactory;

	private String filterName;

	private UI ui = UI.getCurrent();
	private ProgressWindow progressIndicatorWindow;

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
				Page.getCurrent().reload();
			}
		});
		binder.addValueChangeListener(l -> saveButton.setEnabled(binder.isValid()));
		settingsFieldsLayout.addComponent(saveButton);

		Path path = fileSystemService.getFileSystem().getPath(configuration.getRootDir());

		if (Files.exists(path)) {
			Grid<PGSettingsItemTO> grid = new Grid<>("Přehled adresářů");
			grid.setWidth("100%");
			grid.setHeight("500px");

			settingsFieldsLayout.addComponent(grid);

			Column<PGSettingsItemTO, String> nameColumn = grid.addColumn(p -> p.getPath().getFileName().toString())
					.setCaption("Název").setExpandRatio(1);

			grid.addColumn(p -> {
				if (p.getOverviewTO() == null)
					return new Label("Nepoužívá se");
				else
					return new Label(
							"<a href=\"" + getPageURL(photogalleryViewerPageFactory,
									URLIdentifierUtils.createURLIdentifier(p.getOverviewTO().getId(),
											p.getOverviewTO().getName()))
									+ "\" target=\"_blank\">Odkaz</a>",
							ContentMode.HTML);
			}).setRenderer(new ComponentRenderer()).setCaption("Odkaz").setStyleGenerator(item -> "v-align-center");

			grid.addColumn(p -> p.getSize() == null ? "N/A" : HumanBytesSizeFormatter.format(p.getSize()))
					.setCaption("Velikost").setStyleGenerator(item -> "v-align-right");

			grid.addColumn(p -> p.getFilesCount() == null ? "N/A" : p.getFilesCount()).setCaption("Počet souborů")
					.setStyleGenerator(item -> "v-align-right");

			grid.addColumn(p -> {
				if (p.getOverviewTO() == null)
					return null;
				Button button = new Button("Přegenerovat", new Button.ClickListener() {
					private static final long serialVersionUID = 1996102817811495323L;

					@Override
					public void buttonClick(ClickEvent event) {
						UI.getCurrent().addWindow(new ConfirmWindow("Opravdu přegenerovat galerii?", e -> {
							UUID operationId = UUID.randomUUID();

							PhotogalleryTO to = pgService.getPhotogalleryForDetail(p.getOverviewTO().getId());
							progressIndicatorWindow = new ProgressWindow();

							eventBus.subscribe(PGSettingsPage.this);

							PhotogalleryPayloadTO payloadTO = new PhotogalleryPayloadTO(to.getContentNode().getName(),
									to.getPhotogalleryPath(), to.getContentNode().getContentTagsAsStrings(),
									to.getContentNode().isPublicated(), true);
							pgService.modifyPhotogallery(operationId, to.getId(), payloadTO, LocalDateTime.now());
						}));
					}

				});
				button.setStyleName(ValoTheme.BUTTON_LINK);
				return button;
			}).setRenderer(new ComponentRenderer()).setCaption("Přegenerování");

			grid.addColumn(item -> {
				String btnCaption = item.getOverviewTO() == null ? "Smazat adresář" : "Smazat galerii";
				Button button = new Button(btnCaption, new Button.ClickListener() {

					private static final long serialVersionUID = 1996102817811495323L;

					@Override
					public void buttonClick(ClickEvent event) {
						String caption = item.getOverviewTO() == null ? "Opravdu smazat adresář?"
								: "Opravdu smazat galerii (záznam v kategorii a data v adresáři)?";
						UI.getCurrent().addWindow(new ConfirmWindow(caption, e -> deleteItem(item, path, grid)));
					}
				});
				button.setStyleName(ValoTheme.BUTTON_LINK);
				return button;
			}).setRenderer(new ComponentRenderer()).setCaption("Smazání");

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

	protected void deleteItem(PGSettingsItemTO item, Path path, Grid<PGSettingsItemTO> grid) {
		if (item.getOverviewTO() == null) {
			try (Stream<Path> s = Files.walk(item.getPath())) {
				s.sorted(Comparator.reverseOrder()).forEach(p -> {
					try {
						logger.info("Zkouším mazat '" + p.getFileName().toString() + "'");
						Files.delete(p);
					} catch (IOException e2) {
						logger.error("Nezdařilo se smazat adresář " + p.getFileName().toString(), e2);
					}
				});
			} catch (IOException e1) {
				logger.error("Nezdařilo se smazat adresář " + item.getPath().getFileName().toString(), e1);
				WarnWindow warnSubwindow = new WarnWindow(
						"Při mazání adresáře došlo k chybě (" + e1.getMessage() + ")");
				UI.getCurrent().addWindow(warnSubwindow);
			}
		} else {
			if (!pgService.deletePhotogallery(item.getOverviewTO().getId())) {
				WarnWindow warnSubwindow = new WarnWindow("Při mazání galerie se nezdařilo smazat některé soubory.");
				UI.getCurrent().addWindow(warnSubwindow);
			}
		}
		populateGrid(grid, path);
	}

	@Handler
	protected void onProcessStart(final PGProcessStartEvent event) {
		progressIndicatorWindow.runInUI(() -> {
			progressIndicatorWindow.setTotal(event.getCountOfStepsToDo());
			ui.addWindow(progressIndicatorWindow);
		});
	}

	@Handler
	protected void onProcessProgress(PGProcessProgressEvent event) {
		progressIndicatorWindow.runInUI(() -> progressIndicatorWindow.indicateProgress(event.getStepDescription()));
	}

	@Handler
	protected void onProcessResult(final PGProcessResultEvent event) {
		progressIndicatorWindow.runInUI(() -> {
			if (progressIndicatorWindow != null)
				progressIndicatorWindow.close();
			if (event.isSuccess())
				ui.addWindow(new InfoWindow("Přegenerování dopladlo úspěšně"));
			else
				ui.addWindow(new WarnWindow("Při přegenerování došlo k chybám: ", event.getResultDetails()));
		});
		eventBus.unsubscribe(PGSettingsPage.this);
	}

	private Long getFileSize(Path path) {
		try {
			if (!Files.isDirectory(path))
				return Files.size(path);
			try (Stream<Path> stream = Files.list(path)) {
				Long sum = 0L;
				for (Iterator<Path> it = stream.iterator(); it.hasNext();)
					sum += getFileSize(it.next());
				return sum;
			}
		} catch (Exception e) {
			logger.error("Nezdařilo se zjistit velikost souboru " + path.getFileName().toString(), e);
			return null;
		}
	}

	private PGSettingsItemTO createItem(Path path) {
		PhotogalleryRESTOverviewTO to = pgService.getPhotogalleryByDirectory(path.getFileName().toString());
		Long size = getFileSize(path);
		Long filesCount = null;
		try (Stream<Path> stream = Files.list(path)) {
			filesCount = stream.count();
		} catch (IOException e) {
			logger.error("Nezdařilo se zjistit počet položek adresáře " + path.getFileName().toString(), e);
		}
		return new PGSettingsItemTO(path, to, size, filesCount);
	}

	private Stream<PGSettingsItemTO> createStream(Path path) {
		try {
			// zde se úmyslně nezavírá stream, protože se předává dál do vaadin
			return Files.list(path)
					.filter(p -> p.getFileName().toString().contains(filterName == null ? "" : filterName))
					.map(this::createItem);
		} catch (IOException e) {
			logger.error("Nezdařilo se načíst galerie z " + path.getFileName().toString(), e);
			return new ArrayList<PGSettingsItemTO>().stream();
		}
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

	private void populateGrid(Grid<PGSettingsItemTO> grid, Path path) {
		grid.setDataProvider((sortOrder, offset, limit) -> createStream(path).skip(offset).limit(limit),
				() -> (int) count(path));
	}

}
