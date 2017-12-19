package cz.gattserver.grass3.fm.web;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.server.Page;
import com.vaadin.ui.Component;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.JavaScript;
import com.vaadin.ui.Label;
import com.vaadin.ui.Layout;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.renderers.ComponentRenderer;
import com.vaadin.ui.renderers.LocalDateTimeRenderer;

import cz.gattserver.common.util.HumanBytesSizeFormatter;
import cz.gattserver.grass3.exception.GrassPageException;
import cz.gattserver.grass3.fm.FMExplorer;
import cz.gattserver.grass3.fm.FMExplorer.FileProcessState;
import cz.gattserver.grass3.fm.config.FMConfiguration;
import cz.gattserver.grass3.server.GrassRequest;
import cz.gattserver.grass3.services.FileSystemService;
import cz.gattserver.grass3.ui.components.Breadcrumb;
import cz.gattserver.grass3.ui.components.Breadcrumb.BreadcrumbElement;
import cz.gattserver.grass3.ui.components.CreateGridButton;
import cz.gattserver.grass3.ui.components.DeleteGridButton;
import cz.gattserver.grass3.ui.components.GridButton;
import cz.gattserver.grass3.ui.components.ModifyGridButton;
import cz.gattserver.grass3.ui.pages.factories.template.PageFactory;
import cz.gattserver.grass3.ui.pages.template.OneColumnPage;
import cz.gattserver.grass3.ui.util.GridUtils;
import cz.gattserver.grass3.ui.util.UIUtils;
import cz.gattserver.web.common.ui.ImageIcon;
import cz.gattserver.web.common.ui.MultiUpload;

public class FMPage extends OneColumnPage {

	@Resource(name = "fmPageFactory")
	private PageFactory fmPageFactory;

	@Autowired
	private FileSystemService fileSystemService;

	private FileSystem fileSystem;

	/**
	 * FM Explorer s potřebnými daty a metodami pro procházení souborů
	 */
	private FMExplorer explorer;

	/**
	 * Filestable
	 */
	private Grid<Path> grid;

	/**
	 * Hodnota status labelu, když nejsou vybraná žádná pole
	 */
	private String satusLabelStaticValue;

	/**
	 * Status label, vybrané soubory apod.
	 */
	private Label statusLabel;

	/**
	 * Breadcrumb
	 */
	private Breadcrumb breadcrumb;

	public FMPage(GrassRequest request) {
		super(request);
	}

	@Override
	protected Layout createPayload() {
		statusLabel = new Label();
		breadcrumb = new Breadcrumb();

		fileSystem = fileSystemService.getFileSystem();

		StringBuilder builder = new StringBuilder();
		String pathPart;
		while ((pathPart = getRequest().getAnalyzer().getCurrentPathToken()) != null) {
			getRequest().getAnalyzer().shift();
			builder.append(pathPart);
			builder.append("/");
		}
		String path = builder.toString();

		// kontrolu validnosti adresáře je potřeba provést už v init
		explorer = new FMExplorer(path, fileSystem);

		// Bylo potřeba se vrátit do kořene, protože předložený adresář
		// neexistuje nebo není dostupný ? Pokud ano, vyhoď varování.
		switch (explorer.getState()) {
		case SUCCESS:
			// úspěch - pokračujeme
			Page.getCurrent().addPopStateListener(e -> {
				int start = e.getUri().indexOf(getRequest().getContextRoot());
				String fmPath = e.getUri().substring(
						start + getRequest().getContextRoot().length() + 1 + fmPageFactory.getPageName().length());
				if (fmPath.isEmpty() || fmPath.startsWith("/")) {
					if (fmPath.startsWith("/"))
						fmPath = fmPath.substring(1);
					handleGotoDirAction(fileSystem.getPath(fmPath), true);
				} else {
					// úplně jiná stránka, která akorát začíná na
					// "context-root/fm"
				}
			});
			updatePageState();
			break;
		case MISSING:
			UIUtils.showWarning("Cíl neexistuje - vracím se do kořenového adresáře");
			break;
		case NOT_VALID:
			UIUtils.showWarning(
					"Cíl se nachází mimo povolený rozsah souborů k prohlížení - vracím se do kořenového adresáře");
			break;
		case SYSTEM_ERROR:
			UIUtils.showWarning("Z cíle nelze číst - vracím se do kořenového adresáře");
			break;
		default:
			UIUtils.showWarning("Neznámá chyba - vracím se do kořenového adresáře");
			break;
		}

		if (explorer.isPathDerivedFromFile()) {
			// nabídni ke stáhnutí tento souboru TODO
			// ResourceStreamRequestTarget target = new
			// ResourceStreamRequestTarget(
			// new FileResourceStream(absoluteRequestedFile));
			// target.setFileName(absoluteRequestedFile.getName());
			// RequestCycle.get().setRequestTarget(target);
		}

		return super.createPayload();
	}

	@Override
	protected Component createContent() {
		VerticalLayout marginLayout = new VerticalLayout();
		marginLayout.setMargin(true);

		VerticalLayout layout = new VerticalLayout();
		layout.setMargin(true);
		layout.setSpacing(true);

		layout.addComponent(statusLabel);

		createBreadcrumb(layout);
		createFilestable(layout);
		createButtonsLayout(layout);

		marginLayout.addComponent(layout);
		return marginLayout;
	}

	private void createBreadcrumb(VerticalLayout layout) {
		layout.addComponent(breadcrumb);
		populateBreadcrumb();
	}

	private void populateBreadcrumb() {
		// pokud zjistím, že cesta neodpovídá, vyhodím 302 (přesměrování) na
		// aktuální polohu cílové kategorie
		List<BreadcrumbElement> breadcrumbElements = new ArrayList<BreadcrumbElement>();

		Path next = explorer.getCurrentAbsolutePath();
		Path rootPath = explorer.getRootPath();
		do {
			String fileURLFromRoot = explorer.fileFromRoot(next);
			breadcrumbElements.add(new BreadcrumbElement(next.equals(rootPath) ? "/" : next.getFileName().toString(),
					getPageResource(fmPageFactory, fileURLFromRoot)));
			next = next.getParent();
			// pokud je můj předek null nebo jsem mimo povolený rozsah, pak
			// je to konec a je to všechno
		} while (next != null && next.startsWith(rootPath));

		breadcrumb.resetBreadcrumb(breadcrumbElements);
	}

	private void createFilestable(VerticalLayout layout) {
		grid = new Grid<>();
		layout.addComponent(grid);
		grid.setSizeFull();
		grid.setSelectionMode(SelectionMode.MULTI);
		grid.setColumnReorderingAllowed(true);

		grid.addColumn(p -> new Image(null,
				(Files.isDirectory(p) ? ImageIcon.FOLDER_16_ICON : ImageIcon.DOCUMENT_16_ICON).createResource()),
				new ComponentRenderer()).setWidth(GridUtils.ICON_COLUMN_WIDTH).setCaption("");

		grid.addColumn(Path::getFileName).setCaption("Název");

		grid.addColumn(p -> {
			try {
				return HumanBytesSizeFormatter.format(Files.size(p), true);
			} catch (IOException e1) {
				return "err";
			}
		}).setCaption("Velikost").setStyleGenerator(item -> "v-align-right");

		grid.addColumn(p -> {
			try {
				return LocalDateTime.ofInstant(Files.getLastModifiedTime(p).toInstant(), ZoneId.systemDefault());
			} catch (IOException e1) {
				return null;
			}
		}).setRenderer(new LocalDateTimeRenderer("d.MM.yyyy HH:mm")).setCaption("Upraveno");

		grid.addSelectionListener(e -> {
			Set<Path> value = e.getAllSelectedItems();
			if (null == value || value.size() == 0) {
				statusLabel.setValue(satusLabelStaticValue);
			} else {
				switch (value.size()) {
				case 1:
					statusLabel.setValue("Vybrán 1 soubor");
					break;
				case 2:
				case 3:
				case 4:
					statusLabel.setValue("Vybrány " + value.size() + " soubory ");
					break;
				default:
					statusLabel.setValue("Vybráno " + value.size() + " souborů");
				}
			}
		});

		grid.addItemClickListener(e -> {
			if (e.getMouseEventDetails().isDoubleClick()) {
				Path path = e.getItem();
				if (Files.isDirectory(path))
					// UIUtils.redirect(getPageURL(fmPageFactory,
					// explorer.fileFromRoot(path).toString()));
					handleGotoDirAction(path, false);
			} else {
				if (e.getMouseEventDetails().isShiftKey()) {
					if (grid.getSelectedItems().contains(e.getItem()))
						grid.deselect(e.getItem());
					else
						grid.select(e.getItem());
				} else {
					if (grid.getSelectedItems().size() == 1
							&& grid.getSelectedItems().iterator().next().equals(e.getItem())) {
						grid.deselect(e.getItem());
					} else {
						grid.deselectAll();
						grid.select(e.getItem());
					}
				}
			}
		});

		populateGrid();
	}

	private void populateGrid() {
		int size;
		try {
			// +1 za odkaz na nadřazený adresář
			size = (int) Files.list(explorer.getCurrentAbsolutePath()).count() + 1;
		} catch (IOException e) {
			throw new GrassPageException(500, e);
		}
		grid.setDataProvider((sortOrder, offset, limit) -> {
			try {
				return Stream.concat(Stream.of(fileSystem.getPath("..")),
						Files.list(explorer.getCurrentAbsolutePath()).sorted((p1, p2) -> {
							if (Files.isDirectory(p1)) {
								if (Files.isDirectory(p2))
									return p1.getFileName().compareTo(p2);
								return -1;
							} else {
								if (Files.isDirectory(p2))
									return 1;
								return p1.getFileName().compareTo(p2);
							}
						})).skip(offset).limit(limit);
			} catch (IOException e) {
				throw new GrassPageException(500, e);
			}
		}, () -> size);

		// Status label static value
		satusLabelStaticValue = "Zobrazeno " + size + " souborů";
		statusLabel.setValue(satusLabelStaticValue);
	}

	private void createButtonsLayout(VerticalLayout layout) {
		HorizontalLayout buttonsLayout = new HorizontalLayout();
		buttonsLayout.setSpacing(true);
		layout.addComponent(buttonsLayout);

		buttonsLayout.addComponent(new CreateGridButton("Vytvořit nový adresář", e -> handleNewDirectory()));

		MultiUpload multiFileUpload = new MultiUpload("Nahrát soubory") {
			private static final long serialVersionUID = -415832652157894459L;

			public void handleFile(InputStream in, String fileName, String mime, long size) {
				switch (explorer.saveFile(in, fileName)) {
				case SUCCESS:
					// refresh
					populateGrid();
					break;
				case ALREADY_EXISTS:
					UIUtils.showWarning(
							"Soubor '" + fileName + "' nebylo možné uložit - soubor s tímto názvem již existuje.");
					break;
				case NOT_VALID:
					UIUtils.showWarning("Soubor '" + fileName
							+ "' nebylo možné uložit - cílové umístění souboru se nachází mimo povolený rozsah souborů k prohlížení.");
					break;
				default:
					UIUtils.showWarning("Soubor '" + fileName + "' nebylo možné uložit - došlo k systémové chybě.");
				}
			}
		};
		buttonsLayout.addComponent(multiFileUpload);

		GridButton<Path> downloadButton = new GridButton<>("Stáhnout", (e, items) -> handleDownloadAction(items), grid);
		downloadButton.setIcon(ImageIcon.DOWN_16_ICON.createResource());
		buttonsLayout.addComponent(downloadButton);

		GridButton<Path> gotoButton = new GridButton<>("Přejít",
				(e, items) -> handleGotoDirAction(items.iterator().next(), false), grid);
		gotoButton.setIcon(ImageIcon.RIGHT_16_ICON.createResource());
		gotoButton.setEnableResolver(items -> {
			return items.size() == 1 && Files.isDirectory(items.iterator().next());
		});
		buttonsLayout.addComponent(gotoButton);

		buttonsLayout.addComponent(new ModifyGridButton<Path>("Přejmenovat", (e, p) -> handleRenameAction(p), grid));

		buttonsLayout.addComponent(new DeleteGridButton<Path>("Smazat", items -> handleDeleteAction(items), grid));
	}

	private void handleNewDirectory() {
		UI.getCurrent().addWindow(new FileNameWindow("Nový adresář", (s, w) -> {
			switch (explorer.createNewDir(s)) {
			case SUCCESS:
				populateGrid();
				w.close();
				break;
			case ALREADY_EXISTS:
				UIUtils.showWarning("Nezdařilo se vytvořit nový adresář - adresář s tímto jménem již existuje.");
				break;
			case NOT_VALID:
				UIUtils.showWarning(
						"Nezdařilo se vytvořit nový adresář - cílové umístění adresáře se nachází mimo povolený rozsah souborů k prohlížení.");
				break;
			default:
				UIUtils.showWarning("Nezdařilo se vytvořit nový adresář - došlo k systémové chybě.");
			}
		}));
	}

	private void handleDeleteAction(Set<Path> items) {
		FileProcessState overallResult = FileProcessState.SUCCESS;
		for (Path p : items) {
			FileProcessState partialResult = explorer.deleteFile(p);
			if (!partialResult.equals(FileProcessState.SUCCESS))
				overallResult = partialResult;
		}
		if (!overallResult.equals(FileProcessState.SUCCESS))
			UIUtils.showWarning("Některé soubory se nezdařilo smazat.");
		populateGrid();
	}

	private void handleGotoDirAction(Path path, boolean historyNavigation) {
		String dir = (historyNavigation ? path.toString()
				: explorer.getCurrentRelativePath().resolve(path.getFileName()).toString());
		if (FileProcessState.SUCCESS.equals(explorer.tryGotoDir(dir))) {
			populateBreadcrumb();
			populateGrid();
			if (!historyNavigation)
				updatePageState();
		}
	}

	private void handleRenameAction(final Path path) {
		UI.getCurrent().addWindow(new FileNameWindow("Přejmenovat", path.getFileName().toString(), (s, w) -> {
			switch (explorer.renameFile(path, s)) {
			case SUCCESS:
				populateGrid();
				w.close();
				break;
			case ALREADY_EXISTS:
				UIUtils.showWarning("Přejmenování se nezdařilo - soubor s tímto názvem již existuje.");
				break;
			case NOT_VALID:
				UIUtils.showWarning(
						"Přejmenování se nezdařilo - cílové umístění souboru se nachází mimo povolený rozsah souborů k prohlížení.");
				break;
			default:
				UIUtils.showWarning("Přejmenování se nezdařilo - došlo k systémové chybě.");
				break;
			}
		}));
	}

	private void handleDownloadAction(Set<Path> items) {
		Path item = items.iterator().next();
		if (items.size() == 1 && !Files.isDirectory(item)) {
			String url = getRequest().getContextRoot() + FMConfiguration.FM_PATH + "/"
					+ explorer.getCurrentRelativePath() + item.getFileName();
			JavaScript.eval("window.open('" + url + "', '_blank');");
		} else {
			// TODO adresář nebo více souborů stáhne je jako ZIP
		}
	}

	private void updatePageState() {
		// Tohle je potřeba pushovat celé znova od kořene webu, protože jakmile
		// se ve stavu objeví "/", je to bráno jako nový kořen a další pushState
		// nahradí pouze poslední chunk
		StringBuilder sb = new StringBuilder();
		sb.append(getRequest().getContextRoot());
		sb.append("/");
		sb.append(fmPageFactory.getPageName());
		for (Path part : explorer.getCurrentRelativePath()) {
			sb.append("/");
			sb.append(part.toString());
		}
		Page.getCurrent().pushState(sb.toString());
	}

}
