package org.myftp.gattserver.grass3.fm;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.myftp.gattserver.grass3.pages.HomeWindow;
import org.myftp.gattserver.grass3.pages.template.OneColumnPage;
import org.myftp.gattserver.grass3.subwindows.ConfirmSubwindow;
import org.myftp.gattserver.grass3.subwindows.GrassSubWindow;
import org.myftp.gattserver.grass3.util.ReferenceHolder;
import org.vaadin.easyuploads.MultiFileUpload;

import com.vaadin.data.Item;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Validator;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.event.Action;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.terminal.DownloadStream;
import com.vaadin.terminal.ExternalResource;
import com.vaadin.terminal.FileResource;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Link;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

public class FMWindow extends OneColumnPage {

	private static final long serialVersionUID = -950042653154868289L;

	public static final String NAME = "fm";

	/**
	 * FM Explorer s potřebnými daty a metodami pro procházení souborů
	 */
	private FMExplorer explorer;

	/**
	 * Breadcrumb
	 */
	private HorizontalLayout breadcrumbLayout;

	/**
	 * Filestable
	 */
	private Table filestable;

	/**
	 * Upload
	 */
	private MultiFileUpload multiFileUpload;

	/**
	 * Status label, vybrané soubory apod.
	 */
	private Label statusLabel;

	/**
	 * Hodnota status labelu, když nejsou vybraná žádná pole
	 */
	private String satusLabelStaticValue;

	public FMWindow() {
		setName(NAME);
	}

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
	private static final Action[] ACTIONS_GROUP = new Action[] { ACTION_MOVE,
			ACTION_DELETE, ACTION_ZIP, ACTION_DETAILS };

	/**
	 * Akce pro jednotlivce (adresář)
	 */
	private static final Action[] ACTIONS_DIR = new Action[] { ACTION_OPEN,
			ACTION_RENAME, ACTION_MOVE, ACTION_DELETE, ACTION_ZIP,
			ACTION_DETAILS };

	/**
	 * Akce pro jednotlivce (soubor)
	 */
	private static final Action[] ACTIONS_FILE = new Action[] { ACTION_OPEN,
			ACTION_RENAME, ACTION_MOVE, ACTION_DELETE, ACTION_ZIP,
			ACTION_DOWNLOAD, ACTION_DETAILS };

	private class DirNameValidator implements Validator {

		private static final long serialVersionUID = -1239174257195947577L;

		public void validate(Object value) throws InvalidValueException {
			if (explorer.validateNewDirName(value.toString()) == false)
				throw new InvalidValueException(
						"Název adresáře nesmí obsahovat znaky \""
								+ explorer.getInvalidNewDirCharacters() + "\"");
		}

		public boolean isValid(Object value) {
			return explorer.validateNewDirName(value.toString());
		}

	}

	private void initBreadcrumb(VerticalLayout layout) {

		HorizontalLayout breadcrumbBackground = new HorizontalLayout();
		breadcrumbBackground.setStyleName("breadcrumb");
		breadcrumbBackground.setSizeFull();
		layout.addComponent(breadcrumbBackground);
		breadcrumbLayout = new HorizontalLayout();
		breadcrumbBackground.addComponent(breadcrumbLayout);
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

		filestable.addListener(new Table.ValueChangeListener() {

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
						statusLabel.setValue("Vybrány " + markedFiles.size()
								+ " soubory ");
						break;
					default:
						statusLabel.setValue("Vybráno " + markedFiles.size()
								+ " souborů");
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
					filestable.requestRepaint();
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
				if (markedFiles != null && markedFiles.size() > 1
						&& markedFiles.contains(targetFile)) {
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
		return (markedFiles != null && markedFiles.size() > 1 && markedFiles
				.contains(file));
	}

	private void handleDeleteAction(final File file, VerticalLayout layout) {

		final ReferenceHolder<Boolean> groupOperation = ReferenceHolder
				.newBooleanHolder(isOperationTargetSelectedGroup(file));

		Label subWindowLabel = new Label(
				groupOperation.getValue() ? "Opravdu chcete smazat vybrané soubory ?"
						: "Opravdu chcete smazat soubor \"" + file.getName()
								+ "\" ?");

		final Window subwindow = new ConfirmSubwindow(subWindowLabel) {

			private static final long serialVersionUID = 6350190755480244374L;

			@Override
			protected void onConfirm(ClickEvent event) {
				boolean clean = true;

				// skupinově nebo RMB vybraný soubor ?
				if (groupOperation.getValue()) {
					for (File markedFile : markedFiles) {
						clean = clean && explorer.deleteFile(markedFile);
					}
				} else {
					clean = explorer.deleteFile(file);
				}

				// všechno se podařilo smazat
				if (clean) {
					// refresh dir list
					createDirList();
					showInfo("Soubory byly úspěšně odstraněny.");
				} else {
					// něco se nepodařilo
					showWarning("Některé soubory se nezdařilo smazat.");
				}
			}

		};
		addWindow(subwindow);

	}

	private void handleOpenAction(File file) {
		open(new ExternalResource(getURL() + "/" + filePathFromRoot(file)));
	}

	private void handleRenameAction(final File file, VerticalLayout layout) {
		final Window subwindow = new GrassSubWindow("Přejmenovat");
		subwindow.center();
		addWindow(subwindow);

		GridLayout subWindowlayout = new GridLayout(2, 2);
		subwindow.setContent(subWindowlayout);
		subWindowlayout.setMargin(true);
		subWindowlayout.setSpacing(true);

		final TextField newNameField = new TextField("Nový název:");
		newNameField.setValue(file.getName());
		newNameField.addValidator(new DirNameValidator());
		subWindowlayout.addComponent(newNameField, 0, 0, 1, 0);

		Button confirm = new Button("Přejmenovat", new Button.ClickListener() {

			private static final long serialVersionUID = 8490964871266821307L;

			public void buttonClick(ClickEvent event) {
				if (newNameField.isValid() == false)
					return;
				if (explorer.renameFile(file, (String) newNameField.getValue())) {
					showInfo("Soubor byl úspěšně přejmenován.");
					// refresh dir list
					createDirList();
				} else {
					showWarning("Přejmenování se nezdařilo.");
				}

				(subwindow.getParent()).removeWindow(subwindow);
			}
		});

		subWindowlayout.addComponent(confirm, 0, 1);
		subWindowlayout.setComponentAlignment(confirm, Alignment.MIDDLE_CENTER);

		Button close = new Button("Storno", new Button.ClickListener() {

			private static final long serialVersionUID = 8490964871266821307L;

			public void buttonClick(ClickEvent event) {
				(subwindow.getParent()).removeWindow(subwindow);
			}
		});

		subWindowlayout.addComponent(close, 1, 1);
		subWindowlayout.setComponentAlignment(close, Alignment.MIDDLE_CENTER);

		// Zaměř se na nové okno
		subwindow.focus();
	}

	private void createSingleFileDetails(final File file,
			final GrassSubWindow subwindow) {

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
		subWindowlayout.addComponent(
				new Label(String.valueOf(new Date(file.lastModified()))), 1, 1);

		// Čtení
		subWindowlayout.addComponent(new Label("Čtení:"), 0, 2);
		subWindowlayout.addComponent(new Label(file.canRead() ? "Ano" : "Ne"),
				1, 2);

		// Zápis
		subWindowlayout.addComponent(new Label("Zápis:"), 0, 3);
		subWindowlayout.addComponent(new Label(file.canWrite() ? "Ano" : "Ne"),
				1, 3);

		// Spouštění
		subWindowlayout.addComponent(new Label("Spouštění:"), 0, 4);
		subWindowlayout.addComponent(
				new Label(file.canExecute() ? "Ano" : "Ne"), 1, 4);

		// Velikost
		subWindowlayout.addComponent(new Label("Velikost:"), 0, 5);
		List<File> skipList = new ArrayList<File>();
		long size = explorer.getDeepDirSize(file, skipList);
		String humanSize = FMExplorer.humanReadableByteCount(size, true);
		subWindowlayout.addComponent(new Label(humanSize), 1, 5);

		// Jsou započítané všechny soubory podstromu ?
		if (!skipList.isEmpty()) {
			subwindow
					.getParent()
					.showWarning(
							"Některé soubory nemohly být započítány do celkové velikosti.");
		}
		// Velikost (binární)
		subWindowlayout
				.addComponent(
						new Label(FMExplorer
								.humanReadableByteCount(size, false)), 1, 6);

		// OK button
		Button close = new Button("OK", new Button.ClickListener() {

			private static final long serialVersionUID = 8490964871266821307L;

			public void buttonClick(ClickEvent event) {
				(subwindow.getParent()).removeWindow(subwindow);
			}
		});

		subWindowlayout.addComponent(close, 0, 7);
		subWindowlayout.setComponentAlignment(close, Alignment.BOTTOM_LEFT);

	}

	private void createGroupDetails(final GrassSubWindow subwindow) {

		GridLayout subWindowlayout = new GridLayout(2, 4);
		subwindow.setContent(subWindowlayout);
		subWindowlayout.setMargin(true);
		subWindowlayout.setSpacing(true);
		subWindowlayout.setSizeFull();

		// Počet
		subWindowlayout.addComponent(new Label("Počet souborů:"), 0, 0);
		subWindowlayout.addComponent(
				new Label(String.valueOf(markedFiles.size())), 1, 0);

		// Velikost
		subWindowlayout.addComponent(new Label("Velikost:"), 0, 1);
		List<File> skipList = new ArrayList<File>();
		long size = 0;
		for (File file : markedFiles) {
			size += explorer.getDeepDirSize(file, skipList);
		}
		String humanSize = FMExplorer.humanReadableByteCount(size, true);
		subWindowlayout.addComponent(new Label(humanSize), 1, 1);

		// Jsou započítané všechny soubory podstromu ?
		if (!skipList.isEmpty()) {
			subwindow
					.getParent()
					.showInfo(
							"Některé soubory nemohly být započítány do celkové velikosti.");
		}
		// Velikost (binární)
		subWindowlayout
				.addComponent(
						new Label(FMExplorer
								.humanReadableByteCount(size, false)), 1, 2);

		// OK button
		Button close = new Button("OK", new Button.ClickListener() {

			private static final long serialVersionUID = 8490964871266821307L;

			public void buttonClick(ClickEvent event) {
				(subwindow.getParent()).removeWindow(subwindow);
			}
		});

		subWindowlayout.addComponent(close, 0, 3);
		subWindowlayout.setComponentAlignment(close, Alignment.BOTTOM_LEFT);

	}

	private void handleDetailsAction(final File file, VerticalLayout layout) {
		final GrassSubWindow subwindow = new GrassSubWindow("Detail");
		subwindow.center();
		subwindow.setWidth("470px");
		addWindow(subwindow);

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
		open(new FileResource(file, getApplication()));
	}

	@Override
	protected void createContent(VerticalLayout layout) {

		layout.setMargin(true);
		layout.setSpacing(true);

		// Breadcrumb
		initBreadcrumb(layout);

		// Přehled souborů
		initFilestable(layout);

		// Status label
		layout.addComponent(statusLabel = new Label());

		// Vytvoření nového adresáře
		createNewDirPanel(layout);

		// Upload souboru(ů)
		createUploadFileForm(layout);

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
		newDirName.addValidator(new DirNameValidator());
		panelLayout.addComponent(newDirName);

		Button createButton = new Button("Vytvořit",
				new Button.ClickListener() {

					private static final long serialVersionUID = -4315617904120991885L;

					public void buttonClick(ClickEvent event) {
						if (newDirName.isValid() == false)
							return;
						if (explorer.createNewDir(newDirName.getValue()
								.toString())) {
							showInfo("Nový adresář byl úspěšně vytvořen.");
							// refresh dir list
							createDirList();
							// clean
							newDirName.setValue("");
						} else {
							showWarning("Nezdařilo se vytvořit nový adresář.");
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

		multiFileUpload = new MultiFileUpload() {

			private static final long serialVersionUID = -6217699369125272543L;

			@Override
			protected String getAreaText() {
				return "<small>VLOŽ<br/>SOUBORY</small>";
			}

			@Override
			protected void handleFile(File file, String fileName,
					String mimeType, long length) {
				if (explorer.saveFile(file, fileName) == false)
					showWarning("Soubor '" + fileName
							+ "' nebylo možné uložit.");
				else {
					// refresh
					createDirList();
				}
			}
		};
		multiFileUpload.setUploadButtonCaption("Vybrat soubory");
		multiFileUpload.setSizeFull();
		panelLayout.addComponent(multiFileUpload);

	}

	/**
	 * Breakcrumb - cesta k souboru
	 */
	private void createBreadcrumb() {

		// Homelink
		Link homelink = new Link("Domů", new ExternalResource(getApplication()
				.getWindow(HomeWindow.NAME).getURL()));
		homelink.setStyleName("breadcrumb_element");
		breadcrumbLayout.addComponent(homelink);

		// Seznam linků postupně směrem ke kořeni
		List<Link> links = new ArrayList<Link>();
		File next = explorer.getAbsoluteRequestedFile();
		try {
			do {
				String filePathFromRoot = filePathFromRoot(next);
				Link link = new Link(next.getPath().equals(
						explorer.getAbsoluteRootDirPath()) ? "/"
						: next.getName(), new ExternalResource(getURL() + "/"
						+ filePathFromRoot));
				link.setStyleName("breadcrumb_element");
				links.add(link);

				next = next.getParentFile();
			} while (next != null
					&& next.getCanonicalPath().length() >= explorer
							.getAbsoluteRootDirPath().length());
		} catch (IOException e) {
			e.printStackTrace();
			// ...
		}

		// konstrukce breadcrumb v opačném pořadí (správném)
		for (int i = links.size() - 1; i >= 0; i--) {
			Embedded separator = new Embedded();
			separator.setSource(new ThemeResource("img/bullet.png"));
			breadcrumbLayout.addComponent(separator);
			breadcrumbLayout.addComponent(links.get(i));
		}
	}

	private void createDirList() {

		IndexedContainer container = new IndexedContainer();
		container.addContainerProperty(ColumnId.IKONA, Embedded.class, null);
		container.addContainerProperty(ColumnId.NÁZEV, String.class, null);
		container.addContainerProperty(ColumnId.VELIKOST, String.class, "");
		container.addContainerProperty(ColumnId.DATUM, String.class, "");
		container.addContainerProperty(ColumnId.OPRÁVNĚNÍ, String.class, "");
		filestable.setContainerDataSource(container);
		filestable.setColumnWidth(ColumnId.IKONA, 16);
		filestable.setColumnHeader(ColumnId.IKONA, "");

		// filestable.setItemIconPropertyId(ColumnId.ICON); // ikon sloupec
		// filestable.setRowHeaderMode(Table.ROW_HEADER_MODE_ICON_ONLY);
		filestable.setColumnAlignment(ColumnId.VELIKOST, Table.ALIGN_RIGHT);

		List<File> directories = new ArrayList<File>();
		List<File> innerFiles = new ArrayList<File>();

		// Projdi všechny soubory v adresáři
		File[] subfiles = explorer.getAbsoluteRequestedFile().listFiles();
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
		if (!explorer.getAbsoluteRequestedPath().equals(
				explorer.getAbsoluteRootDirPath())) {
			File parent = explorer.getAbsoluteRequestedFile().getParentFile();

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
			item.getItemProperty(ColumnId.DATUM).setValue(
					new Date(file.lastModified()));
			item.getItemProperty(ColumnId.OPRÁVNĚNÍ).setValue(
					(file.canExecute() ? "x" : "-")
							+ (file.canRead() ? "r" : "-")
							+ (file.canWrite() ? "w" : "-"));
		}

		// Soubory
		for (File file : innerFiles) {
			Item item = filestable.addItem(file);
			Embedded icon = new Embedded();
			icon.setSource(new ThemeResource("img/tags/present_16.png"));
			item.getItemProperty(ColumnId.IKONA).setValue(icon);

			item.getItemProperty(ColumnId.NÁZEV).setValue(file.getName());
			item.getItemProperty(ColumnId.VELIKOST).setValue(
					FMExplorer.humanReadableByteCount(file.length(), true));
			item.getItemProperty(ColumnId.DATUM).setValue(
					new Date(file.lastModified()));
			item.getItemProperty(ColumnId.OPRÁVNĚNÍ).setValue(
					(file.canExecute() ? "x" : "-")
							+ (file.canRead() ? "r" : "-")
							+ (file.canWrite() ? "w" : "-"));
		}

		// Status label static value
		satusLabelStaticValue = "Zobrazeno " + directories.size()
				+ " adresářů, " + innerFiles.size() + " souborů";
		statusLabel.setValue(satusLabelStaticValue);

		filestable.addListener(new ItemClickEvent.ItemClickListener() {
			private static final long serialVersionUID = 2068314108919135281L;

			public void itemClick(ItemClickEvent event) {
				if (event.isDoubleClick()) {
					File file = (File) event.getItemId();
					if (file.isDirectory()) {
						open(new ExternalResource(getURL() + "/"
								+ filePathFromRoot(file)));
					} else {
						open(new FileResource(file, getApplication()));
					}
				}
			}
		});

	}

	private String filePathFromRoot(File file) {
		try {
			String path = explorer.filePathFromRoot(file);
			return path;
		} catch (IOException e) {
			e.printStackTrace();
			// tohle by se nemělo stát
			return "/";
		}
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
			shortText = longText.substring(0, limit / 2 - 1)
					+ "..."
					+ longText.substring(longText.length() - 1
							- (limit / 2 - 2));
		}
		return shortText;
	}

	@Override
	public DownloadStream handleURI(URL context, String relativeUri) {

		// stav disku se může měnit, je potřeba být "svěží"
		try {
			explorer = new FMExplorer(relativeUri);
		} catch (IOException e) {
			open(new ExternalResource(getApplication().getWindow("500")
					.getURL()));
		}

		// Bylo potřeba se vrátit do kořene, protože předložený adresář
		// neexistuje nebo není dostupný ? Pokud ano, vyhoď varování.
		if (explorer.isForcedToRoot()) {
			showWarning("Požadovaný cíl nebylo možné navštívit, zobrazuji kořenový adresář");
		}

		if (explorer.isPathDerivedFromFile()) {
			// nabídni ke stáhnutí tento souboru TODO
			// ResourceStreamRequestTarget target = new
			// ResourceStreamRequestTarget(
			// new FileResourceStream(absoluteRequestedFile));
			// target.setFileName(absoluteRequestedFile.getName());
			// RequestCycle.get().setRequestTarget(target);
		}

		/**
		 * Aktualizuj přehled breadcrumb
		 */
		breadcrumbLayout.removeAllComponents();
		createBreadcrumb();

		/**
		 * Aktualizuj přehled adresářů
		 */
		createDirList();

		/**
		 * Je potřeba aktualizovat upload tool dle exploreru
		 */
		multiFileUpload.setRootDirectory(explorer.getTmpPath());

		return super.handleURI(context, relativeUri);
	}
}
