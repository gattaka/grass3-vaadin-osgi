package org.myftp.gattserver.grass3.fm.web;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import org.myftp.gattserver.grass3.fm.FMExplorer;
import org.myftp.gattserver.grass3.fm.FMExplorer.FileProcessState;
import org.myftp.gattserver.grass3.fm.config.FMConfiguration;
import org.myftp.gattserver.grass3.pages.factories.template.IPageFactory;
import org.myftp.gattserver.grass3.pages.template.OneColumnPage;
import org.myftp.gattserver.grass3.subwindows.ConfirmWindow;
import org.myftp.gattserver.grass3.subwindows.GrassWindow;
import org.myftp.gattserver.grass3.subwindows.InfoWindow;
import org.myftp.gattserver.grass3.subwindows.WarnWindow;
import org.myftp.gattserver.grass3.template.Breadcrumb;
import org.myftp.gattserver.grass3.template.Breadcrumb.BreadcrumbElement;
import org.myftp.gattserver.grass3.template.MultiUpload;
import org.myftp.gattserver.grass3.ui.util.ComparableStringDate;
import org.myftp.gattserver.grass3.ui.util.GrassRequest;
import org.myftp.gattserver.grass3.util.HumanBytesSizeCreator;
import org.myftp.gattserver.grass3.util.ReferenceHolder;

import com.vaadin.data.Item;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.event.Action;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.server.ExternalResource;
import com.vaadin.server.FileDownloader;
import com.vaadin.server.FileResource;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Component;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Link;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.Align;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

public class FMPage extends OneColumnPage {

	private static final long serialVersionUID = -950042653154868289L;

	@Resource(name = "fmPageFactory")
	private IPageFactory fmPageFactory;

	/**
	 * FM Explorer s potřebnými daty a metodami pro procházení souborů
	 */
	private FMExplorer explorer;

	/**
	 * Filestable
	 */
	private Table filestable;

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
	private Set<File> markedFiles;
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
	private static final Action[] ACTIONS_GROUP = new Action[] { ACTION_MOVE, ACTION_DELETE, ACTION_ZIP, ACTION_DETAILS };

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
	protected void init() {

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
		try {
			explorer = new FMExplorer(builder.toString());
		} catch (IOException e) {
			showError500();
			return;
		}

		// Bylo potřeba se vrátit do kořene, protože předložený adresář
		// neexistuje nebo není dostupný ? Pokud ano, vyhoď varování.
		switch (explorer.getState()) {
		case SUCCESS:
			// úspěch - pokračujeme
			break;
		case MISSING:
			showWarning("Cíl neexistuje - vracím se do kořenového adresáře");
			break;
		case NOT_VALID:
			showWarning("Cíl se nachází mimo povolený rozsah souborů k prohlížení - vracím se do kořenového adresáře");
			break;
		case SYSTEM_ERROR:
			showWarning("Z cíle nelze číst - vracím se do kořenového adresáře");
			break;
		default:
			showWarning("Neznámá chyba - vracím se do kořenového adresáře");
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

		super.init();
	}

	private void initBreadcrumb(VerticalLayout layout) {

		layout.addComponent(breadcrumb);

		// pokud zjistím, že cesta neodpovídá, vyhodím 302 (přesměrování) na
		// aktuální polohu cílové kategorie
		List<BreadcrumbElement> breadcrumbElements = new ArrayList<BreadcrumbElement>();

		File next = explorer.getRequestedFile();
		String rootPath = explorer.getRootFile().getPath();
		try {
			do {

				String filePathFromRoot = explorer.fileURLFromRoot(next);
				breadcrumbElements.add(new BreadcrumbElement(next.getPath().equals(rootPath) ? "/" : next.getName(),
						getPageResource(fmPageFactory, filePathFromRoot)));

				next = next.getParentFile();

				// pokud je můj předek null nebo jsem mimo povolený rozsah, pak
				// je to konec a je to všechno
			} while (next != null && next.getPath().length() >= rootPath.length());

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		breadcrumb.resetBreadcrumb(breadcrumbElements);
	}

	private void initFilestable(VerticalLayout layout) {

		filestable = new Table();
		layout.addComponent(filestable);
		filestable.setSizeFull();
		filestable.setSelectable(true);
		filestable.setMultiSelect(true);
		filestable.setImmediate(true);
		filestable.setColumnReorderingAllowed(true);
		filestable.setColumnCollapsingAllowed(true);

		filestable.addValueChangeListener(new Table.ValueChangeListener() {

			private static final long serialVersionUID = -6605391938100454104L;

			/**
			 * Můžu si dovolit potlačit varování přetypování na Set s parametrem
			 * File, protože vím, že v těch values jsou jenom File - uživatel
			 * tohle nemůže ovlivnit
			 */
			@SuppressWarnings("unchecked")
			public void valueChange(ValueChangeEvent event) {
				Set<?> value = (Set<File>) event.getProperty().getValue();
				if (null == value || value.size() == 0) {
					markedFiles = null;
					statusLabel.setValue(satusLabelStaticValue);
				} else {
					markedFiles = (Set<File>) filestable.getValue();
					switch (markedFiles.size()) {
					case 1:
						statusLabel.setValue("Vybrán 1 soubor");
						break;
					case 2:
					case 3:
					case 4:
						statusLabel.setValue("Vybrány " + markedFiles.size() + " soubory ");
						break;
					default:
						statusLabel.setValue("Vybráno " + markedFiles.size() + " souborů");
					}
				}
				/**
				 * Je potřeba, aby se přegenerovaly context Menu - viditelnost
				 * položek je totiž závislá na selected souborech
				 */
				// TODO - tohle je dočasný workaround, kterým se trigne
				// getActions z handleru - jedině tak se projeví vlastnost v
				// menu skupinového označení - problém je ale v tom, že pak
				// blbne informace o range při shift-klávesa výběru
				if (markedFiles != null && markedFiles.size() > 1) {
					// filestable.setVisible(false);
					// filestable.setVisible(true);
					// filestable.requestRepaint();
					filestable.refreshRowCache();
				}
			}

		});

		createContextMenu(layout);
	}

	private void createContextMenu(final VerticalLayout layout) {
		filestable.addActionHandler(new Action.Handler() {

			private static final long serialVersionUID = -1204234416330259274L;

			public Action[] getActions(Object target, Object sender) {

				// ?? Tohle se prostě sem může dostat ...
				if (target == null)
					return new Action[0];

				File targetFile = (File) target;

				// Akce pro skupinu se nabízí v případě, že:
				// - RMB kliká na soubor z vybrané skupiny
				// - skupina obsahuje více souborů
				if (markedFiles != null && markedFiles.size() > 1 && markedFiles.contains(targetFile)) {
					return ACTIONS_GROUP;
				} else {
					if (targetFile.isDirectory())
						return ACTIONS_DIR;
					else
						return ACTIONS_FILE;
				}
			}

			/**
			 * Je bezpečné zde potlačit warning protože se do filestable dávají
			 * jenom items typu File
			 */
			@SuppressWarnings("unchecked")
			private Set<File> getValues() {
				return (Set<File>) filestable.getValue();
			}

			public void handleAction(Action action, Object sender, Object target) {
				File targetFile = (File) target;

				/**
				 * Pokud RMB neklikl na položku již v označených položkách, zruš
				 * označení položek a vyber jenom tuto
				 */
				Set<File> selected = getValues();
				if (!selected.contains(targetFile)) {
					selected = new HashSet<File>();
					selected.add(targetFile);
					filestable.setValue(selected);
				}

				if (action == ACTION_OPEN) {
					handleOpenAction(targetFile);
				} else if (action == ACTION_RENAME) {
					handleRenameAction(targetFile, layout);
				} else if (action == ACTION_DETAILS) {
					handleDetailsAction(targetFile, layout);
				} else if (action == ACTION_DELETE) {
					handleDeleteAction(targetFile, layout);
				} else if (action == ACTION_DOWNLOAD) {
					handleDownloadFile(targetFile);
				}
			}

		});
	}

	/**
	 * Pokud jsou nějaké soubory označeny a je v nich i ten, který byl cíle RMB,
	 * pak to ber jako skupinovou operaci. Tato funkce v zásadě rozhoduje o tom,
	 * zda mám brát jako "target" operace selected skupinu (true) nebo vybraný
	 * soubor (false)
	 */
	private boolean isOperationTargetSelectedGroup(File file) {
		return (markedFiles != null && markedFiles.size() > 1 && markedFiles.contains(file));
	}

	private void handleDeleteAction(final File file, VerticalLayout layout) {

		final ReferenceHolder<Boolean> groupOperation = ReferenceHolder
				.newBooleanHolder(isOperationTargetSelectedGroup(file));

		Label subWindowLabel = new Label(groupOperation.getValue() ? "Opravdu chcete smazat vybrané soubory ?"
				: "Opravdu chcete smazat soubor \"" + file.getName() + "\" ?");

		final Window subwindow = new ConfirmWindow(subWindowLabel) {

			private static final long serialVersionUID = 6350190755480244374L;

			@Override
			protected void onConfirm(ClickEvent event) {
				FileProcessState overallResult = FileProcessState.SUCCESS;

				// skupinově nebo RMB vybraný soubor ?
				if (groupOperation.getValue()) {
					FileProcessState partialResult;
					for (File markedFile : markedFiles) {
						partialResult = explorer.deleteFile(markedFile);
						if (partialResult.equals(FileProcessState.SUCCESS) == false) {
							overallResult = partialResult;
						}
					}
				} else {
					overallResult = explorer.deleteFile(file);
				}

				// všechno se podařilo smazat
				if (overallResult.equals(FileProcessState.SUCCESS)) {
					// refresh dir list
					createDirList();
					showInfo("Soubory byly úspěšně odstraněny.");
				} else {
					// něco se nepodařilo
					showWarning("Některé soubory se nezdařilo smazat.");
				}
			}

		};
		getGrassUI().addWindow(subwindow);

	}

	private void handleOpenAction(File file) {
		try {
			redirect(getPageURL(fmPageFactory, explorer.fileURLFromRoot(file).toString()));
		} catch (IOException e) {
			e.printStackTrace();
			showWarning("Soubor se nepodařilo otevřít");
		}
	}

	private void handleRenameAction(final File file, VerticalLayout layout) {
		final Window subwindow = new GrassWindow("Přejmenovat");
		subwindow.center();
		getGrassUI().addWindow(subwindow);

		GridLayout subWindowlayout = new GridLayout(2, 2);
		subwindow.setContent(subWindowlayout);
		subWindowlayout.setMargin(true);
		subWindowlayout.setSpacing(true);

		final TextField newNameField = new TextField("Nový název:");
		newNameField.setValue(file.getName());
		subWindowlayout.addComponent(newNameField, 0, 0, 1, 0);

		Button confirm = new Button("Přejmenovat", new Button.ClickListener() {

			private static final long serialVersionUID = 8490964871266821307L;

			public void buttonClick(ClickEvent event) {
				if (newNameField.isValid() == false)
					return;
				switch (explorer.renameFile(file, (String) newNameField.getValue())) {
				case SUCCESS:
					showInfo("Soubor byl úspěšně přejmenován.");
					// refresh dir list
					createDirList();
					break;
				case ALREADY_EXISTS:
					showWarning("Přejmenování se nezdařilo - soubor s tímto názvem již existuje.");
					break;
				case NOT_VALID:
					showWarning("Přejmenování se nezdařilo - cílové umístění souboru se nachází mimo povolený rozsah souborů k prohlížení.");
					break;
				default:
					showWarning("Přejmenování se nezdařilo - došlo k systémové chybě.");
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

	private void createSingleFileDetails(final File file, final GrassWindow subwindow) {

		GridLayout subWindowlayout = new GridLayout(2, 8);
		subwindow.setContent(subWindowlayout);
		subWindowlayout.setMargin(true);
		subWindowlayout.setSpacing(true);
		subWindowlayout.setSizeFull();

		// ikona
		Embedded icon = new Embedded();
		if (file.isDirectory())
			icon.setSource(new ThemeResource("img/tags/folder_16.png"));
		else
			icon.setSource(new ThemeResource("img/tags/present_16.png"));
		subWindowlayout.addComponent(icon, 0, 0);

		// název
		subWindowlayout.addComponent(new Label(file.getName()), 1, 0);

		// Datum
		subWindowlayout.addComponent(new Label("Datum úpravy:"), 0, 1);
		subWindowlayout.addComponent(new Label(String.valueOf(new Date(file.lastModified()))), 1, 1);

		// Čtení
		subWindowlayout.addComponent(new Label("Čtení:"), 0, 2);
		subWindowlayout.addComponent(new Label(file.canRead() ? "Ano" : "Ne"), 1, 2);

		// Zápis
		subWindowlayout.addComponent(new Label("Zápis:"), 0, 3);
		subWindowlayout.addComponent(new Label(file.canWrite() ? "Ano" : "Ne"), 1, 3);

		// Spouštění
		subWindowlayout.addComponent(new Label("Spouštění:"), 0, 4);
		subWindowlayout.addComponent(new Label(file.canExecute() ? "Ano" : "Ne"), 1, 4);

		// Velikost
		subWindowlayout.addComponent(new Label("Velikost:"), 0, 5);
		List<File> skipList = new ArrayList<File>();
		long size = explorer.getDeepDirSize(file, skipList);
		String humanSize = HumanBytesSizeCreator.format(size, true);
		subWindowlayout.addComponent(new Label(humanSize), 1, 5);

		// Jsou započítané všechny soubory podstromu ?
		if (!skipList.isEmpty()) {
			getGrassUI().addWindow(new WarnWindow("Některé soubory nemohly být započítány do celkové velikosti."));
		}
		// Velikost (binární)
		subWindowlayout.addComponent(new Label(HumanBytesSizeCreator.format(size, false)), 1, 6);

		// OK button
		Button close = new Button("OK", new Button.ClickListener() {

			private static final long serialVersionUID = 8490964871266821307L;

			public void buttonClick(ClickEvent event) {
				subwindow.close();
			}
		});

		subWindowlayout.addComponent(close, 0, 7);
		subWindowlayout.setComponentAlignment(close, Alignment.BOTTOM_LEFT);

	}

	private void createGroupDetails(final GrassWindow subwindow) {

		GridLayout subWindowlayout = new GridLayout(2, 4);
		subwindow.setContent(subWindowlayout);
		subWindowlayout.setMargin(true);
		subWindowlayout.setSpacing(true);
		subWindowlayout.setSizeFull();

		// Počet
		subWindowlayout.addComponent(new Label("Počet souborů:"), 0, 0);
		subWindowlayout.addComponent(new Label(String.valueOf(markedFiles.size())), 1, 0);

		// Velikost
		subWindowlayout.addComponent(new Label("Velikost:"), 0, 1);
		List<File> skipList = new ArrayList<File>();
		long size = 0;
		for (File file : markedFiles) {
			size += explorer.getDeepDirSize(file, skipList);
		}
		String humanSize = HumanBytesSizeCreator.format(size, true);
		subWindowlayout.addComponent(new Label(humanSize), 1, 1);

		// Jsou započítané všechny soubory podstromu ?
		if (!skipList.isEmpty()) {
			getGrassUI().addWindow(new InfoWindow("Některé soubory nemohly být započítány do celkové velikosti."));
		}
		// Velikost (binární)
		subWindowlayout.addComponent(new Label(HumanBytesSizeCreator.format(size, false)), 1, 2);

		// OK button
		Button close = new Button("OK", new Button.ClickListener() {

			private static final long serialVersionUID = 8490964871266821307L;

			public void buttonClick(ClickEvent event) {
				subwindow.close();
			}
		});

		subWindowlayout.addComponent(close, 0, 3);
		subWindowlayout.setComponentAlignment(close, Alignment.BOTTOM_LEFT);

	}

	private void handleDetailsAction(final File file, VerticalLayout layout) {
		final GrassWindow subwindow = new GrassWindow("Detail");
		subwindow.center();
		subwindow.setWidth("470px");
		subwindow.setHeight("300px");
		getGrassUI().addWindow(subwindow);

		// Skupina nebo RMB vybraný soubor
		if (isOperationTargetSelectedGroup(file)) {
			createGroupDetails(subwindow);
		} else {
			createSingleFileDetails(file, subwindow);
		}

		// Zaměř se na nové okno
		subwindow.focus();
	}

	private void handleDownloadFile(final File file) {
		GrassWindow dlWindow = new GrassWindow("Stáhnout " + file.getName()) {

			private static final long serialVersionUID = 926172618599746150L;

			{
				HorizontalLayout horizontalLayout = new HorizontalLayout();
				horizontalLayout.setSpacing(true);
				this.addComponent(horizontalLayout);

				Button button = new Button("Stáhnout");
				horizontalLayout.addComponent(button);
				horizontalLayout.setComponentAlignment(button, Alignment.MIDDLE_CENTER);
				FileDownloader downloader = new FileDownloader(new FileResource(file));
				downloader.extend(button);
				try {
					String url = getRequest().getContextRoot() + FMConfiguration.FM_PATH + "/"
							+ explorer.fileURLFromRoot(explorer.getRequestedFile()) + file.getName();
					Link link;
					horizontalLayout.addComponent(link = new Link(url, new ExternalResource(url)));
					horizontalLayout.setComponentAlignment(link, Alignment.MIDDLE_CENTER);
				} catch (Exception e) {
					throw new AssertionError();
				}
			}

		};
		getGrassUI().addWindow(dlWindow);
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
		panelLayout.addComponent(newDirName);

		Button createButton = new Button("Vytvořit", new Button.ClickListener() {

			private static final long serialVersionUID = -4315617904120991885L;

			public void buttonClick(ClickEvent event) {
				if (newDirName.isValid() == false)
					return;
				switch (explorer.createNewDir(newDirName.getValue().toString())) {
				case SUCCESS:
					showInfo("Nový adresář byl úspěšně vytvořen.");
					// refresh dir list
					createDirList();
					// clean
					newDirName.setValue("");
					break;
				case ALREADY_EXISTS:
					showWarning("Nezdařilo se vytvořit nový adresář - adresář s tímto jménem již existuje.");
					break;
				case NOT_VALID:
					showWarning("Nezdařilo se vytvořit nový adresář - cílové umístění adresáře se nachází mimo povolený rozsah souborů k prohlížení.");
					break;
				default:
					showWarning("Nezdařilo se vytvořit nový adresář - došlo k systémové chybě.");
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

			public void handleFile(File file, String fileName, String mime, long size) {
				switch (explorer.saveFile(file, fileName)) {
				case SUCCESS:
					// refresh
					createDirList();
					break;
				case ALREADY_EXISTS:
					showWarning("Soubor '" + fileName + "' nebylo možné uložit - soubor s tímto názvem již existuje.");
					break;
				case NOT_VALID:
					showWarning("Soubor '"
							+ fileName
							+ "' nebylo možné uložit - cílové umístění souboru se nachází mimo povolený rozsah souborů k prohlížení.");
					break;
				default:
					showWarning("Soubor '" + fileName + "' nebylo možné uložit - došlo k systémové chybě.");
				}
			}

		};

		// multiFileUpload = new MultiFileUpload() {
		//
		// private static final long serialVersionUID = -6217699369125272543L;
		//
		// @Override
		// protected String getAreaText() {
		// return "<small>VLOŽ<br/>SOUBORY</small>";
		// }
		//
		// @Override
		// protected void handleFile(File file, String fileName, String
		// mimeType, long length) {
		// switch (explorer.saveFile(file, fileName)) {
		// case SUCCESS:
		// // refresh
		// createDirList();
		// break;
		// case ALREADY_EXISTS:
		// showWarning("Soubor '" + fileName +
		// "' nebylo možné uložit - soubor s tímto názvem již existuje.");
		// break;
		// case NOT_VALID:
		// showWarning("Soubor '"
		// + fileName
		// +
		// "' nebylo možné uložit - cílové umístění souboru se nachází mimo povolený rozsah souborů k prohlížení.");
		// break;
		// default:
		// showWarning("Soubor '" + fileName +
		// "' nebylo možné uložit - došlo k systémové chybě.");
		// }
		// }
		// };
		// multiFileUpload.setRootDirectory(explorer.getTmpDirFile().getAbsolutePath());
		// multiFileUpload.setUploadButtonCaption("Vybrat soubory");

		multiFileUpload.setSizeFull();
		panelLayout.addComponent(multiFileUpload);

	}

	private void createDirList() {

		IndexedContainer container = new IndexedContainer();
		container.addContainerProperty(ColumnId.IKONA, Embedded.class, null);
		container.addContainerProperty(ColumnId.NÁZEV, String.class, null);
		container.addContainerProperty(ColumnId.VELIKOST, String.class, "");
		container.addContainerProperty(ColumnId.DATUM, ComparableStringDate.class, new ComparableStringDate(null));
		container.addContainerProperty(ColumnId.OPRÁVNĚNÍ, String.class, "");
		filestable.setContainerDataSource(container);
		filestable.setColumnWidth(ColumnId.IKONA, 16);
		filestable.setColumnHeader(ColumnId.IKONA, "");

		// filestable.setItemIconPropertyId(ColumnId.ICON); // ikon sloupec
		// filestable.setRowHeaderMode(Table.ROW_HEADER_MODE_ICON_ONLY);
		filestable.setColumnAlignment(ColumnId.VELIKOST, Align.RIGHT);

		List<File> directories = new ArrayList<File>();
		List<File> innerFiles = new ArrayList<File>();

		// Projdi všechny soubory v adresáři
		File[] subfiles = explorer.getRequestedFile().listFiles();
		if (subfiles == null) {
			// getSession().error("Nezdařilo se číst z adresáře");
		} else {
			for (File file : subfiles) {
				if (file.isDirectory()) {
					directories.add(file);
				} else {
					innerFiles.add(file);
				}
			}
		}

		// Předek - pouze pokud nejsem v kořeni
		if (!explorer.getRequestedFile().getPath().equals(explorer.getRootFile().getPath())) {
			File parent = explorer.getRequestedFile().getParentFile();

			Item item = filestable.addItem(parent);
			Embedded icon = new Embedded();
			icon.setSource(new ThemeResource("img/tags/folder_16.png"));
			item.getItemProperty(ColumnId.IKONA).setValue(icon);
			item.getItemProperty(ColumnId.NÁZEV).setValue("..");
		}

		// Adresáře
		for (File file : directories) {
			Item item = filestable.addItem(file);
			Embedded icon = new Embedded();
			icon.setSource(new ThemeResource("img/tags/folder_16.png"));
			item.getItemProperty(ColumnId.IKONA).setValue(icon);

			item.getItemProperty(ColumnId.NÁZEV).setValue(file.getName());
			item.getItemProperty(ColumnId.DATUM).setValue(new ComparableStringDate(new Date(file.lastModified())));
			item.getItemProperty(ColumnId.OPRÁVNĚNÍ).setValue(
					(file.canExecute() ? "x" : "-") + (file.canRead() ? "r" : "-") + (file.canWrite() ? "w" : "-"));
		}

		// Soubory
		for (File file : innerFiles) {
			Item item = filestable.addItem(file);
			Embedded icon = new Embedded();
			icon.setSource(new ThemeResource("img/tags/present_16.png"));
			item.getItemProperty(ColumnId.IKONA).setValue(icon);

			item.getItemProperty(ColumnId.NÁZEV).setValue(file.getName());
			item.getItemProperty(ColumnId.VELIKOST).setValue(HumanBytesSizeCreator.format(file.length(), true));
			item.getItemProperty(ColumnId.DATUM).setValue(new ComparableStringDate(new Date(file.lastModified())));
			item.getItemProperty(ColumnId.OPRÁVNĚNÍ).setValue(
					(file.canExecute() ? "x" : "-") + (file.canRead() ? "r" : "-") + (file.canWrite() ? "w" : "-"));
		}

		// Status label static value
		satusLabelStaticValue = "Zobrazeno " + directories.size() + " adresářů, " + innerFiles.size() + " souborů";
		statusLabel.setValue(satusLabelStaticValue);

		filestable.addItemClickListener(new ItemClickEvent.ItemClickListener() {
			private static final long serialVersionUID = 2068314108919135281L;

			public void itemClick(ItemClickEvent event) {
				if (event.isDoubleClick()) {
					File file = (File) event.getItemId();
					if (file.isDirectory()) {
						try {
							redirect(getPageURL(fmPageFactory, explorer.fileURLFromRoot(file).toString()));
						} catch (IOException e) {
							e.printStackTrace();
							showWarning("Nezdařilo se otevřít soubor");
						}
					} else {
						handleDownloadFile(file);
					}
				}
			}
		});

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
