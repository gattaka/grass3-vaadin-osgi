package cz.gattserver.grass3.fm.web;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.ui.Component;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.JavaScript;
import com.vaadin.ui.Label;
import com.vaadin.ui.Layout;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
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
import cz.gattserver.grass3.ui.util.UIUtils;
import cz.gattserver.web.common.ui.ImageIcon;
import cz.gattserver.web.common.ui.MultiUpload;

public class FMPage extends OneColumnPage {

	@Resource(name = "fmPageFactory")
	private PageFactory fmPageFactory;

	@Autowired
	private FileSystemService fileSystemService;

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

		StringBuilder builder = new StringBuilder();
		String pathPart;
		while ((pathPart = getRequest().getAnalyzer().getCurrentPathToken()) != null) {
			getRequest().getAnalyzer().shift();
			builder.append(pathPart);
			builder.append("/");
		}

		// kontrolu validnosti adresáře je potřeba provést už v init
		explorer = new FMExplorer(builder.toString(), fileSystemService.getFileSystem());

		// Bylo potřeba se vrátit do kořene, protože předložený adresář
		// neexistuje nebo není dostupný ? Pokud ano, vyhoď varování.
		switch (explorer.getState()) {
		case SUCCESS:
			// úspěch - pokračujeme
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

		// pokud zjistím, že cesta neodpovídá, vyhodím 302 (přesměrování) na
		// aktuální polohu cílové kategorie
		List<BreadcrumbElement> breadcrumbElements = new ArrayList<BreadcrumbElement>();

		Path next = explorer.getRequestedPath();
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
					UIUtils.redirect(getPageURL(fmPageFactory, explorer.fileFromRoot(path).toString()));
			}
		});

		populateGrid();
	}

	private void populateGrid() {
		int size;
		try {
			size = (int) Files.list(explorer.getRequestedPath()).count();
		} catch (IOException e) {
			throw new GrassPageException(500, e);
		}
		grid.setDataProvider((sortOrder, offset, limit) -> {
			try {
				return Files.list(explorer.getRequestedPath()).skip(offset).limit(limit);
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
		buttonsLayout
				.addComponent(new ModifyGridButton<Path>("Přejmenovat soubor", (e, p) -> handleRenameAction(p), grid));
		buttonsLayout
				.addComponent(new DeleteGridButton<Path>("Smazat soubory", items -> handleDeleteAction(items), grid));

		GridButton<Path> gotoButton = new GridButton<>("Přejít",
				(e, items) -> handleOpenAction(items.iterator().next()), grid);
		gotoButton.setIcon(ImageIcon.RIGHT_16_ICON.createResource());
		buttonsLayout.addComponent(gotoButton);

		GridButton<Path> downloadButton = new GridButton<>("Stáhnout", (e, items) -> handleDownloadAction(items), grid);
		gotoButton.setIcon(ImageIcon.DOWN_16_ICON.createResource());
		buttonsLayout.addComponent(downloadButton);

		MultiUpload multiFileUpload = new MultiUpload() {
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

	private void handleOpenAction(Path path) {
		UIUtils.redirect(getPageURL(fmPageFactory, explorer.fileFromRoot(path).toString()));
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
		if (items.size() > 1) {
			// TODO měl by se rozhodovat a pokud bude více souborů, stáhne je
			// jako ZIP
		} else {
			String url = getRequest().getContextRoot() + FMConfiguration.FM_PATH + "/"
					+ explorer.fileFromRoot(explorer.getRequestedPath()) + items.iterator().next().getFileName();
			JavaScript.eval("window.open('" + url + "', '_blank');");
		}
	}

}
