package cz.gattserver.grass3.fm.web;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.event.Action;
import com.vaadin.server.ExternalResource;
import com.vaadin.server.FileDownloader;
import com.vaadin.server.FileResource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Component;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Layout;
import com.vaadin.ui.Link;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextField;
import com.vaadin.ui.TreeGrid;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

import cz.gattserver.common.util.ReferenceHolder;
import cz.gattserver.grass3.exception.GrassPageException;
import cz.gattserver.grass3.fm.FMExplorer;
import cz.gattserver.grass3.fm.FMExplorer.FileProcessState;
import cz.gattserver.grass3.fm.config.FMConfiguration;
import cz.gattserver.grass3.server.GrassRequest;
import cz.gattserver.grass3.services.FileSystemService;
import cz.gattserver.grass3.ui.components.Breadcrumb;
import cz.gattserver.grass3.ui.components.Breadcrumb.BreadcrumbElement;
import cz.gattserver.grass3.ui.pages.factories.template.PageFactory;
import cz.gattserver.grass3.ui.pages.template.OneColumnPage;
import cz.gattserver.grass3.ui.util.UIUtils;
import cz.gattserver.web.common.ui.MultiUpload;
import cz.gattserver.web.common.ui.window.ConfirmWindow;
import cz.gattserver.web.common.ui.window.WebWindow;

public class FMPage extends OneColumnPage {

	private Logger logger = LoggerFactory.getLogger(FMPage.class);

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
	private Grid<Path> filestable;

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

	/**
	 * Přehled sloupců tabulky souborů
	 */
	private enum ColumnId {
		IKONA, NÁZEV, PŘÍPONA, VELIKOST, DATUM, OPRÁVNĚNÍ;
	}

	/**
	 * Akce kontextového menu tabulky souborů
	 */
	private Set<Path> markedPaths;
	private static final Action ACTION_OPEN = new Action("Otevřít");
	private static final Action ACTION_RENAME = new Action("Přejmenovat");
	private static final Action ACTION_MOVE = new Action("Přesunout");
	private static final Action ACTION_DELETE = new Action("Smazat");
	private static final Action ACTION_ZIP = new Action("Stáhnout jako .ZIP");
	private static final Action ACTION_DOWNLOAD = new Action("Stáhnout");
	private static final Action ACTION_DETAILS = new Action("Detaily");

	/**
	 * Akce pro skupinu
	 */
	private static final Action[] ACTIONS_GROUP = new Action[] { ACTION_MOVE, ACTION_DELETE, ACTION_ZIP,
			ACTION_DETAILS };

	/**
	 * Akce pro jednotlivce (adresář)
	 */
	private static final Action[] ACTIONS_DIR = new Action[] { ACTION_OPEN, ACTION_RENAME, ACTION_MOVE, ACTION_DELETE,
			ACTION_ZIP, ACTION_DETAILS };

	/**
	 * Akce pro jednotlivce (soubor)
	 */
	private static final Action[] ACTIONS_FILE = new Action[] { ACTION_OPEN, ACTION_RENAME, ACTION_MOVE, ACTION_DELETE,
			ACTION_ZIP, ACTION_DOWNLOAD, ACTION_DETAILS };

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

	private void initBreadcrumb(VerticalLayout layout) {

		layout.addComponent(breadcrumb);

		// pokud zjistím, že cesta neodpovídá, vyhodím 302 (přesměrování) na
		// aktuální polohu cílové kategorie
		List<BreadcrumbElement> breadcrumbElements = new ArrayList<BreadcrumbElement>();

		Path next = explorer.getRequestedPath();
		Path rootPath = explorer.getRootPath();
		try {
			do {
				String fileURLFromRoot = explorer.fileURLFromRoot(next);
				breadcrumbElements
						.add(new BreadcrumbElement(next.equals(rootPath) ? "/" : next.getFileName().toString(),
								getPageResource(fmPageFactory, fileURLFromRoot)));
				next = next.getParent();
				// pokud je můj předek null nebo jsem mimo povolený rozsah, pak
				// je to konec a je to všechno
			} while (next != null && next.startsWith(rootPath));
		} catch (IOException e) {
			throw new GrassPageException(500, e);
		}

		breadcrumb.resetBreadcrumb(breadcrumbElements);
	}

	private void initFilestable(VerticalLayout layout) {

		filestable = new Grid<>();
		layout.addComponent(filestable);
		filestable.setSizeFull();
		filestable.setSelectionMode(SelectionMode.MULTI);
		filestable.setColumnReorderingAllowed(true);

		filestable.addSelectionListener(e -> {
			// Můžu si dovolit potlačit varování přetypování na Set s parametrem
			// File, protože vím, že v těch values jsou jenom File - uživatel
			// tohle nemůže ovlivnit
			Set<Path> value = e.getAllSelectedItems();
			if (null == value || value.size() == 0) {
				markedPaths = null;
				statusLabel.setValue(satusLabelStaticValue);
			} else {
				markedPaths = value;
				switch (markedPaths.size()) {
				case 1:
					statusLabel.setValue("Vybrán 1 soubor");
					break;
				case 2:
				case 3:
				case 4:
					statusLabel.setValue("Vybrány " + markedPaths.size() + " soubory ");
					break;
				default:
					statusLabel.setValue("Vybráno " + markedPaths.size() + " souborů");
				}
			}
		});

		filestable.addItemClickListener(e -> {
			if (e.getMouseEventDetails().isDoubleClick()) {
				Path path = e.getItem();
				if (Files.isDirectory(path)) {
					try {
						UIUtils.redirect(getPageURL(fmPageFactory, explorer.fileURLFromRoot(path).toString()));
					} catch (IOException ex) {
						logger.error("Nezdařilo se otevřít soubor", ex);
						UIUtils.showWarning("Nezdařilo se otevřít soubor");
					}
				} else {
					handleDownloadFile(path);
				}
			}
		});

	}

	/**
	 * Pokud jsou nějaké soubory označeny a je v nich i ten, který byl cílem
	 * RMB, pak to ber jako skupinovou operaci. Tato funkce v zásadě rozhoduje o
	 * tom, zda mám brát jako "target" operace selected skupinu (true) nebo
	 * vybraný soubor (false)
	 */
	private boolean isOperationTargetSelectedGroup(Path path) {
		return (markedPaths != null && markedPaths.size() > 1 && markedPaths.contains(path));
	}

	private void handleDeleteAction(final Path path, VerticalLayout layout) {

		final ReferenceHolder<Boolean> groupOperation = ReferenceHolder
				.newBooleanHolder(isOperationTargetSelectedGroup(path));

		Label subWindowLabel = new Label(groupOperation.getValue() ? "Opravdu chcete smazat vybrané soubory ?"
				: "Opravdu chcete smazat soubor \"" + path.getFileName() + "\" ?");

		final Window subwindow = new ConfirmWindow(subWindowLabel, e -> {
			FileProcessState overallResult = FileProcessState.SUCCESS;

			// skupinově nebo RMB vybraný soubor ?
			if (groupOperation.getValue()) {
				FileProcessState partialResult;
				for (Path markedPath : markedPaths) {
					partialResult = explorer.deleteFile(markedPath);
					if (partialResult.equals(FileProcessState.SUCCESS) == false) {
						overallResult = partialResult;
					}
				}
			} else {
				overallResult = explorer.deleteFile(path);
			}

			// všechno se podařilo smazat
			if (overallResult.equals(FileProcessState.SUCCESS)) {
				// refresh dir list
				createDirList();
				UIUtils.showInfo("Soubory byly úspěšně odstraněny.");
			} else {
				// něco se nepodařilo
				UIUtils.showWarning("Některé soubory se nezdařilo smazat.");
			}
		});
		UIUtils.getGrassUI().addWindow(subwindow);

	}

	private void handleOpenAction(Path path) {
		try {
			UIUtils.redirect(getPageURL(fmPageFactory, explorer.fileURLFromRoot(path).toString()));
		} catch (IOException e) {
			e.printStackTrace();
			UIUtils.showWarning("Soubor se nepodařilo otevřít");
		}
	}

	private void handleRenameAction(final Path path, VerticalLayout layout) {
		final Window subwindow = new WebWindow("Přejmenovat");
		subwindow.center();
		UIUtils.getGrassUI().addWindow(subwindow);

		GridLayout subWindowlayout = new GridLayout(2, 2);
		subwindow.setContent(subWindowlayout);
		subWindowlayout.setMargin(true);
		subWindowlayout.setSpacing(true);

		final TextField newNameField = new TextField("Nový název:");
		newNameField.setValue(path.getFileName().toString());
		subWindowlayout.addComponent(newNameField, 0, 0, 1, 0);

		Button confirm = new Button("Přejmenovat", new Button.ClickListener() {

			private static final long serialVersionUID = 8490964871266821307L;

			public void buttonClick(ClickEvent event) {
				switch (explorer.renameFile(path, (String) newNameField.getValue())) {
				case SUCCESS:
					UIUtils.showInfo("Soubor byl úspěšně přejmenován.");
					// refresh dir list
					createDirList();
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
				subwindow.close();
			}
		});

		subWindowlayout.addComponent(confirm, 0, 1);
		subWindowlayout.setComponentAlignment(confirm, Alignment.MIDDLE_CENTER);

		Button close = new Button("Storno", new Button.ClickListener() {

			private static final long serialVersionUID = 8490964871266821307L;

			public void buttonClick(ClickEvent event) {
				subwindow.close();
			}
		});

		subWindowlayout.addComponent(close, 1, 1);
		subWindowlayout.setComponentAlignment(close, Alignment.MIDDLE_CENTER);

		// Zaměř se na nové okno
		subwindow.focus();
	}

	private void handleDownloadFile(final Path file) {
		WebWindow dlWindow = new WebWindow("Stáhnout " + file.getFileName()) {

			private static final long serialVersionUID = 926172618599746150L;

			{
				HorizontalLayout horizontalLayout = new HorizontalLayout();
				horizontalLayout.setSpacing(true);
				this.addComponent(horizontalLayout);

				Button button = new Button("Stáhnout");
				horizontalLayout.addComponent(button);
				horizontalLayout.setComponentAlignment(button, Alignment.MIDDLE_CENTER);
				FileDownloader downloader = new FileDownloader(new FileResource(file.toFile()));
				downloader.extend(button);
				try {
					String url = getRequest().getContextRoot() + FMConfiguration.FM_PATH + "/"
							+ explorer.fileURLFromRoot(explorer.getRequestedPath()) + file.getFileName();
					Link link;
					horizontalLayout.addComponent(link = new Link(url, new ExternalResource(url)));
					horizontalLayout.setComponentAlignment(link, Alignment.MIDDLE_CENTER);
				} catch (Exception e) {
					throw new AssertionError();
				}
			}

		};
		UIUtils.getGrassUI().addWindow(dlWindow);
		// Page.getCurrent().open(, file.getName(), false);
	}

	@Override
	protected Component createContent() {

		VerticalLayout layout = new VerticalLayout();

		layout.setMargin(true);
		layout.setSpacing(true);

		// Status label
		layout.addComponent(statusLabel);

		// Breadcrumb
		initBreadcrumb(layout);

		// Přehled souborů - tabulka
		initFilestable(layout);

		// Přehled souborů - obsah (bude se později aktualizovat, proto není
		// součástí initFilestable)
		createDirList();

		// Vytvoření nového adresáře
		createNewDirPanel(layout);

		// Upload souboru(ů)
		createUploadFileForm(layout);

		return layout;

	}

	private void createNewDirPanel(VerticalLayout layout) {
		Panel panel = new Panel("Nový adresář");
		layout.addComponent(panel);

		HorizontalLayout panelBackgroudLayout = new HorizontalLayout();
		panelBackgroudLayout.setSizeFull();
		panel.setContent(panelBackgroudLayout);

		HorizontalLayout panelLayout = new HorizontalLayout();
		panelLayout.setSpacing(true);
		panelLayout.setMargin(true);
		panelBackgroudLayout.addComponent(panelLayout);

		final TextField newDirName = new TextField();
		newDirName.setWidth("200px");
		// TODO
		newDirName.setRequiredIndicatorVisible(true);
		panelLayout.addComponent(newDirName);

		Button createButton = new Button("Vytvořit", new Button.ClickListener() {

			private static final long serialVersionUID = -4315617904120991885L;

			public void buttonClick(ClickEvent event) {
				if (newDirName.getValue() == null)
					return;
				switch (explorer.createNewDir(newDirName.getValue().toString())) {
				case SUCCESS:
					UIUtils.showInfo("Nový adresář byl úspěšně vytvořen.");
					// refresh dir list
					createDirList();
					// clean
					newDirName.setValue("");
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

			}
		});
		panelLayout.addComponent(createButton);

	}

	private void createUploadFileForm(VerticalLayout layout) {

		Panel panel = new Panel("Nahrát soubory");
		layout.addComponent(panel);

		HorizontalLayout panelBackgroudLayout = new HorizontalLayout();
		panelBackgroudLayout.setSizeFull();
		panel.setContent(panelBackgroudLayout);

		VerticalLayout panelLayout = new VerticalLayout();
		panelLayout.setSpacing(true);
		panelLayout.setMargin(true);
		panelBackgroudLayout.addComponent(panelLayout);

		MultiUpload multiFileUpload = new MultiUpload() {
			private static final long serialVersionUID = -415832652157894459L;

			public void handleFile(InputStream in, String fileName, String mime, long size) {
				switch (explorer.saveFile(in, fileName)) {
				case SUCCESS:
					// refresh
					createDirList();
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

		multiFileUpload.setSizeFull();
		panelLayout.addComponent(multiFileUpload);

	}

	private void createDirList() {

		int size;
		try {
			size = (int) Files.list(explorer.getRequestedPath()).count();
		} catch (IOException e) {
			throw new GrassPageException(500, e);
		}
		filestable.setDataProvider((sortOrder, offset, limit) -> {
			try {
				return Files.list(explorer.getRequestedPath()).skip(offset).limit(limit);
			} catch (IOException e) {
				throw new GrassPageException(500, e);
			}
		}, () -> size);

		// IndexedContainer container = new IndexedContainer();
		// container.addContainerProperty(ColumnId.IKONA, Embedded.class, null);
		// container.addContainerProperty(ColumnId.NÁZEV, String.class, null);
		// container.addContainerProperty(ColumnId.VELIKOST, String.class, "");
		// // container.addContainerProperty(ColumnId.DATUM,
		// // ComparableStringDate.class, new ComparableStringDate(null));
		// container.addContainerProperty(ColumnId.OPRÁVNĚNÍ, String.class, "");
		// filestable.setContainerDataSource(container);
		// filestable.setColumnWidth(ColumnId.IKONA, 16);
		// filestable.setColumnHeader(ColumnId.IKONA, "");
		//
		// // filestable.setItemIconPropertyId(ColumnId.ICON); // ikon sloupec
		// // filestable.setRowHeaderMode(Table.ROW_HEADER_MODE_ICON_ONLY);
		// filestable.setColumnAlignment(ColumnId.VELIKOST, Align.RIGHT);
		//
		// List<File> directories = new ArrayList<File>();
		// List<File> innerFiles = new ArrayList<File>();
		//
		// // Projdi všechny soubory v adresáři
		// File[] subfiles = ;
		// if (subfiles == null) {
		// // getSession().error("Nezdařilo se číst z adresáře");
		// } else {
		// for (File file : subfiles) {
		// if (file.isDirectory()) {
		// directories.add(file);
		// } else {
		// innerFiles.add(file);
		// }
		// }
		// }
		//
		// // Předek - pouze pokud nejsem v kořeni
		// if
		// (!explorer.getRequestedPath().getPath().equals(explorer.getRootPath().getPath()))
		// {
		// File parent = explorer.getRequestedPath().getParentFile();
		//
		// Item item = filestable.addItem(parent);
		// Embedded icon = new Embedded();
		// icon.setSource(ImageIcon.FOLDER_16_ICON.createResource());
		// item.getItemProperty(ColumnId.IKONA).setValue(icon);
		// item.getItemProperty(ColumnId.NÁZEV).setValue("..");
		// }
		//
		// // Adresáře
		// for (File file : directories) {
		// Item item = filestable.addItem(file);
		// Embedded icon = new Embedded();
		// icon.setSource(ImageIcon.FOLDER_16_ICON.createResource());
		// item.getItemProperty(ColumnId.IKONA).setValue(icon);
		//
		// item.getItemProperty(ColumnId.NÁZEV).setValue(file.getName());
		// // item.getItemProperty(ColumnId.DATUM).setValue(new
		// // ComparableStringDate(new Date(file.lastModified())));
		// item.getItemProperty(ColumnId.OPRÁVNĚNÍ).setValue(
		// (file.canExecute() ? "x" : "-") + (file.canRead() ? "r" : "-") +
		// (file.canWrite() ? "w" : "-"));
		// }
		//
		// // Soubory
		// for (File file : innerFiles) {
		// Item item = filestable.addItem(file);
		// Embedded icon = new Embedded();
		// icon.setSource(ImageIcon.PRESENT_16_ICON.createResource());
		// item.getItemProperty(ColumnId.IKONA).setValue(icon);
		//
		// item.getItemProperty(ColumnId.NÁZEV).setValue(file.getName());
		// item.getItemProperty(ColumnId.VELIKOST).setValue(HumanBytesSizeFormatter.format(file.length(),
		// true));
		// // item.getItemProperty(ColumnId.DATUM).setValue(new
		// // ComparableStringDate(new Date(file.lastModified())));
		// item.getItemProperty(ColumnId.OPRÁVNĚNÍ).setValue(
		// (file.canExecute() ? "x" : "-") + (file.canRead() ? "r" : "-") +
		// (file.canWrite() ? "w" : "-"));
		// }

		// Status label static value
		satusLabelStaticValue = "Zobrazeno " + size + " souborů";
		statusLabel.setValue(satusLabelStaticValue);

	}

	/**
	 * Zkrátí text, ponechá konec a začátek
	 * 
	 * @param longText
	 *            dlouhá verze textu
	 * @return krátká verze textu
	 */
	public static String shortText(final String longText) {
		return shortText(longText, 25);
	}

	public static String shortText(final String longText, int limit) {
		String shortText = longText;
		if (longText.length() > limit) {
			shortText = longText.substring(0, limit / 2 - 1) + "..."
					+ longText.substring(longText.length() - 1 - (limit / 2 - 2));
		}
		return shortText;
	}

}
