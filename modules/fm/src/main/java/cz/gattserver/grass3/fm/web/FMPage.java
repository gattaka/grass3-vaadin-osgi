package cz.gattserver.grass3.fm.web;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileSystem;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.server.Page;
import com.vaadin.server.StreamResource;
import com.vaadin.server.StreamResource.StreamSource;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServletRequest;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.Button;
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
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.renderers.ComponentRenderer;
import com.vaadin.ui.renderers.LocalDateTimeRenderer;
import com.vaadin.ui.themes.ValoTheme;

import cz.gattserver.common.util.CZAmountFormatter;
import cz.gattserver.grass3.fm.FMExplorer;
import cz.gattserver.grass3.fm.FileProcessState;
import cz.gattserver.grass3.fm.interfaces.FMItemTO;
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
import cz.gattserver.web.common.ui.window.WebWindow;
import net.glxn.qrgen.javase.QRCode;

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
	private Grid<FMItemTO> grid;

	/**
	 * Status label, vybrané soubory apod.
	 */
	private Label statusLabel;

	/**
	 * Breadcrumb
	 */
	private Breadcrumb breadcrumb;

	private String urlBase;

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

		explorer = new FMExplorer(fileSystem);
		FileProcessState result = explorer.goToDir(path);

		switch (result) {
		case SUCCESS:
			// úspěch - pokračujeme
			Page.getCurrent().addPopStateListener(e -> {
				if (FileProcessState.SUCCESS.equals(explorer.goToDirByURL(getRequest().getContextRoot(),
						fmPageFactory.getPageName(), e.getUri()))) {
					refreshView();
					updatePageState();
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

		VaadinRequest vaadinRequest = getRequest().getVaadinRequest();
		HttpServletRequest httpServletRequest = ((VaadinServletRequest) vaadinRequest).getHttpServletRequest();
		System.out.println(vaadinRequest.getContextPath());
		System.out.println(httpServletRequest.getPathInfo());

		String fullURL = httpServletRequest.getRequestURL().toString();
		String urlEndPart = httpServletRequest.getPathInfo();
		urlBase = fullURL.substring(0, fullURL.length() - urlEndPart.length());

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
		for (FMItemTO c : explorer.getBreadcrumbChunks())
			breadcrumbElements
					.add(new BreadcrumbElement(c.getName(), getPageResource(fmPageFactory, c.getPathFromFMRoot())));
		breadcrumb.resetBreadcrumb(breadcrumbElements);
	}

	private void createFilesGrid(VerticalLayout layout) {
		grid = new Grid<>();
		layout.addComponent(grid);
		grid.setSizeFull();
		grid.setSelectionMode(SelectionMode.MULTI);
		grid.setColumnReorderingAllowed(true);

		grid.addColumn(
				p -> new Image(null,
						(p.isDirectory() ? ImageIcon.FOLDER_16_ICON : ImageIcon.DOCUMENT_16_ICON).createResource()),
				new ComponentRenderer()).setWidth(GridUtils.ICON_COLUMN_WIDTH).setCaption("");

		grid.addColumn(FMItemTO::getName).setCaption("Název").setExpandRatio(1);
		grid.addColumn(FMItemTO::getSize).setCaption("Velikost").setStyleGenerator(item -> "v-align-right");
		grid.addColumn(to -> {
			if (to.isDirectory())
				return new Label();
			String link = explorer.getDownloadLink(getRequest().getContextRoot(), to.getName());
			return new Label("<a href='" + link + "' target='_blank'>Stažení</a>", ContentMode.HTML);
		}).setRenderer(new ComponentRenderer()).setCaption("Stažení");

		grid.addColumn(to -> {
			if (to.isDirectory())
				return new Label();
			String link = explorer.getDownloadLink(urlBase, to.getName());
			Button button = new Button("QR", new Button.ClickListener() {
				private static final long serialVersionUID = 1996102817811495323L;

				@Override
				public void buttonClick(ClickEvent event) {
					WebWindow ww = new WebWindow("QR");
					Image image = new Image(link, new StreamResource(new StreamSource() {
						private static final long serialVersionUID = -5705256069486765282L;

						@Override
						public InputStream getStream() {
							try {
								File file = QRCode.from(link).file();
								return new FileInputStream(file);
							} catch (IOException e) {
								e.printStackTrace();
								return null;
							}
						}
					}, to.getName()));
					ww.addComponent(image);
					UI.getCurrent().addWindow(ww);
				}
			});
			button.setDescription(link);
			button.setStyleName(ValoTheme.BUTTON_LINK);
			return button;
		}).setRenderer(new ComponentRenderer()).setCaption("QR");

		grid.addColumn(FMItemTO::getLastModified).setRenderer(new LocalDateTimeRenderer("d.MM.yyyy HH:mm"))
				.setCaption("Upraveno");

		grid.addSelectionListener(e -> {
			Set<FMItemTO> value = e.getAllSelectedItems();
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

	private void handleGridDblClick(FMItemTO item) {
		if (item.isDirectory())
			handleGotoDirFromCurrentDirAction(item);
		else
			handleDownloadAction(item);
	}

	private void handleGridSingleClick(FMItemTO item, boolean shift) {
		if (shift) {
			if (grid.getSelectedItems().contains(item))
				grid.deselect(item);
			else
				grid.select(item);
		} else {
			if (grid.getSelectedItems().size() == 1 && grid.getSelectedItems().iterator().next().equals(item)) {
				grid.deselect(item);
			} else {
				grid.deselectAll();
				grid.select(item);
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

			public void fileUploadFinished(InputStream in, String fileName, String mime, long size,
					int filesLeftInQueue) {
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

		GridButton<FMItemTO> downloadButton = new GridButton<>("Stáhnout", this::handleDownloadAction, grid);
		downloadButton.setIcon(ImageIcon.DOWN_16_ICON.createResource());
		buttonsLayout.addComponent(downloadButton);

		GridButton<FMItemTO> gotoButton = new GridButton<>("Přejít",
				items -> handleGotoDirFromCurrentDirAction(items.iterator().next()), grid);
		gotoButton.setIcon(ImageIcon.RIGHT_16_ICON.createResource());
		gotoButton.setEnableResolver(items -> items.size() == 1 && items.iterator().next().isDirectory());

		buttonsLayout.addComponent(gotoButton);
		buttonsLayout.addComponent(new ModifyGridButton<FMItemTO>("Přejmenovat", this::handleRenameAction, grid));
		buttonsLayout.addComponent(new DeleteGridButton<FMItemTO>("Smazat", this::handleDeleteAction, grid));
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

	private void handleDeleteAction(Set<FMItemTO> items) {
		FileProcessState overallResult = FileProcessState.SUCCESS;
		for (FMItemTO p : items) {
			FileProcessState partialResult = explorer.deleteFile(p.getName());
			if (!partialResult.equals(FileProcessState.SUCCESS))
				overallResult = partialResult;
		}
		if (!overallResult.equals(FileProcessState.SUCCESS))
			UIUtils.showWarning("Některé soubory se nezdařilo smazat.");
		populateGrid();
	}

	private void handleGotoDirFromCurrentDirAction(FMItemTO item) {
		if (FileProcessState.SUCCESS.equals(explorer.goToDirFromCurrentDir(item.getName()))) {
			refreshView();
			updatePageState();
		}
	}

	private void refreshView() {
		populateBreadcrumb();
		populateGrid();
	}

	private void handleRenameAction(final FMItemTO item) {
		UI.getCurrent().addWindow(new FileNameWindow("Přejmenovat", item.getName(), (s, w) -> {
			switch (explorer.renameFile(item.getName(), s)) {
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

	private void handleDownloadAction(FMItemTO item) {
		String link = explorer.getDownloadLink(getRequest().getContextRoot(), item.getName());
		JavaScript.eval("window.open('" + link + "', '_blank');");
	}

	private void handleDownloadAction(Set<FMItemTO> items) {
		FMItemTO item = items.iterator().next();
		if (items.size() == 1 && !item.isDirectory()) {
			handleDownloadAction(item);
		} else {
			// TODO adresář nebo více souborů stáhne je jako ZIP
		}
	}

	private void updatePageState() {
		// Tohle je potřeba pushovat celé znova od kořene webu, protože jakmile
		// se ve stavu objeví "/", je to bráno jako nový kořen a další pushState
		// nahradí pouze poslední chunk
		String currentURL = explorer.getCurrentURL(getRequest().getContextRoot(), fmPageFactory.getPageName());
		Page.getCurrent().pushState(currentURL);
	}

}
