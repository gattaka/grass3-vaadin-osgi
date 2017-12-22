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

import cz.gattserver.common.util.CZAmountFormatter;
import cz.gattserver.common.util.HumanBytesSizeFormatter;
import cz.gattserver.grass3.fm.FMExplorer;
import cz.gattserver.grass3.fm.FileProcessState;
import cz.gattserver.grass3.fm.config.FMConfiguration;
import cz.gattserver.grass3.fm.interfaces.PathChunkTO;
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

	private final CZAmountFormatter selectFormatter;
	private final CZAmountFormatter listFormatter;
	private String listFormatterValue;

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
	 * Status label, vybrané soubory apod.
	 */
	private Label statusLabel;

	/**
	 * Breadcrumb
	 */
	private Breadcrumb breadcrumb;

	public FMPage(GrassRequest request) {
		super(request);
		selectFormatter = new CZAmountFormatter("Vybrán %d soubor", "Vybrány %d soubory", "Vybráno %d souborů");
		listFormatter = new CZAmountFormatter("Zobrazen %d soubor", "Zobrazeny %d soubory", "Zobrazeno %d souborů");
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
				// Odparsuj počátek http://host//context-root/fm a získej
				// lokální cestu v rámci FM modulu
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
		createFilesGrid(layout);
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
		List<BreadcrumbElement> breadcrumbElements = new ArrayList<>();
		for (PathChunkTO c : explorer.getBreadcrumbChunks())
			breadcrumbElements.add(new BreadcrumbElement(c.getName(), getPageResource(fmPageFactory, c.getPath())));
		breadcrumb.resetBreadcrumb(breadcrumbElements);
	}

	private void createFilesGrid(VerticalLayout layout) {
		grid = new Grid<>();
		layout.addComponent(grid);
		grid.setSizeFull();
		grid.setSelectionMode(SelectionMode.MULTI);
		grid.setColumnReorderingAllowed(true);

		grid.addColumn(p -> new Image(null,
				(Files.isDirectory(p) ? ImageIcon.FOLDER_16_ICON : ImageIcon.DOCUMENT_16_ICON).createResource()),
				new ComponentRenderer()).setWidth(GridUtils.ICON_COLUMN_WIDTH).setCaption("");

		grid.addColumn(Path::getFileName).setCaption("Název").setExpandRatio(1);

		grid.addColumn(p -> {
			try {
				Long size = explorer.getDeepDirSize(p);
				return size == null ? "" : HumanBytesSizeFormatter.format(size, true);
			} catch (IOException e1) {
				return "n/a";
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
			statusLabel.setValue(value.isEmpty() ? listFormatterValue : selectFormatter.format(value.size()));
		});

		grid.addItemClickListener(e -> {
			if (e.getMouseEventDetails().isDoubleClick())
				handleGridDblClick(e.getItem());
			else
				handleGridSingleClick(e.getItem(), e.getMouseEventDetails().isShiftKey());
		});

		populateGrid();
	}

	private void handleGridDblClick(Path path) {
		if (Files.isDirectory(path))
			handleGotoDirAction(path, false);
		else
			handleDownloadAction(path);
	}

	private void handleGridSingleClick(Path path, boolean shift) {
		if (shift) {
			if (grid.getSelectedItems().contains(path))
				grid.deselect(path);
			else
				grid.select(path);
		} else {
			if (grid.getSelectedItems().size() == 1 && grid.getSelectedItems().iterator().next().equals(path)) {
				grid.deselect(path);
			} else {
				grid.deselectAll();
				grid.select(path);
			}
		}
	}

	private void populateGrid() {
		int size = explorer.listCount();
		grid.setDataProvider((sortOrder, offset, limit) -> explorer.listing(offset, limit), () -> size);
		listFormatterValue = listFormatter.format(size);
		statusLabel.setValue(listFormatterValue);
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
		gotoButton.setEnableResolver(items -> items.size() == 1 && Files.isDirectory(items.iterator().next()));

		buttonsLayout.addComponent(gotoButton);
		buttonsLayout.addComponent(new ModifyGridButton<Path>("Přejmenovat", (e, p) -> handleRenameAction(p), grid));
		buttonsLayout.addComponent(new DeleteGridButton<Path>("Smazat", this::handleDeleteAction, grid));
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

	private void handleDownloadAction(Path item) {
		StringBuilder sb = new StringBuilder();
		sb.append(getRequest().getContextRoot());
		sb.append("/");
		sb.append(FMConfiguration.FM_PATH);
		for (Path part : explorer.getCurrentRelativePath()) {
			sb.append("/");
			sb.append(part.toString());
		}
		sb.append("/");
		sb.append(item.getFileName().toString());
		JavaScript.eval("window.open('" + sb.toString() + "', '_blank');");
	}

	private void handleDownloadAction(Set<Path> items) {
		Path item = items.iterator().next();
		if (items.size() == 1 && !Files.isDirectory(item)) {
			handleDownloadAction(item);
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
