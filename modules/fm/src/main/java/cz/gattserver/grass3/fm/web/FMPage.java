package cz.gattserver.grass3.fm.web;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.FileSystem;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.Grid.Column;
import com.vaadin.flow.component.grid.Grid.SelectionMode;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.textfield.TextFieldVariant;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MultiFileMemoryBuffer;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.renderer.IconRenderer;
import com.vaadin.flow.data.renderer.LocalDateTimeRenderer;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.WildcardParameter;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.server.VaadinRequest;
import com.vaadin.flow.server.VaadinServletRequest;

import cz.gattserver.common.util.CZAmountFormatter;
import cz.gattserver.grass3.fm.FMExplorer;
import cz.gattserver.grass3.fm.FileProcessState;
import cz.gattserver.grass3.fm.interfaces.FMItemTO;
import cz.gattserver.grass3.services.FileSystemService;
import cz.gattserver.grass3.ui.components.Breadcrumb;
import cz.gattserver.grass3.ui.components.Breadcrumb.BreadcrumbElement;
import cz.gattserver.grass3.ui.components.button.CreateGridButton;
import cz.gattserver.grass3.ui.components.button.DeleteGridButton;
import cz.gattserver.grass3.ui.components.button.GridButton;
import cz.gattserver.grass3.ui.components.button.ModifyGridButton;
import cz.gattserver.grass3.ui.pages.factories.template.PageFactory;
import cz.gattserver.grass3.ui.pages.template.GrassPage;
import cz.gattserver.grass3.ui.pages.template.OneColumnPage;
import cz.gattserver.grass3.ui.util.ButtonLayout;
import cz.gattserver.grass3.ui.util.UIUtils;
import cz.gattserver.web.common.ui.ImageIcon;
import cz.gattserver.web.common.ui.window.WebDialog;
import net.glxn.qrgen.javase.QRCode;

@Route("fm")
public class FMPage extends OneColumnPage implements HasUrlParameter<String> {

	private static final long serialVersionUID = -5884444775720831930L;

	@Resource(name = "fmPageFactory")
	private PageFactory fmPageFactory;

	@Autowired
	private FileSystemService fileSystemService;

	private final CZAmountFormatter selectFormatter;
	private final CZAmountFormatter listFormatter;
	private String listFormatterValue;

	private FileSystem fileSystem;

	private String filterName;

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
	private Span statusLabel;

	/**
	 * Breadcrumb
	 */
	private Breadcrumb breadcrumb;

	private String urlBase;
	private String parameter;

	@Override
	public void setParameter(BeforeEvent event, @WildcardParameter String parameter) {
		this.parameter = parameter;
		init();
	}

	public FMPage() {
		selectFormatter = new CZAmountFormatter("Vybrán %d soubor", "Vybrány %d soubory", "Vybráno %d souborů");
		listFormatter = new CZAmountFormatter("Zobrazen %d soubor", "Zobrazeny %d soubory", "Zobrazeno %d souborů");
	}

	@Override
	protected void createColumnContent(Div layout) {

		statusLabel = new Span();
		breadcrumb = new Breadcrumb();

		fileSystem = fileSystemService.getFileSystem();

		String path = parameter;

		explorer = new FMExplorer(fileSystem);
		FileProcessState result = explorer.goToDir(path);

		switch (result) {
		case SUCCESS:
			// úspěch - pokračujeme
			// TODO
			// Page.getCurrent().addPopStateListener(e -> {
			// if
			// (FileProcessState.SUCCESS.equals(explorer.goToDirByURL(getRequest().getContextRoot(),
			// fmPageFactory.getPageName(), e.getUri()))) {
			// refreshView();
			// updatePageState();
			// }
			// });
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

		// TODO
		VaadinRequest vaadinRequest = VaadinRequest.getCurrent();
		VaadinServletRequest vaadinServletRequest = (VaadinServletRequest) vaadinRequest;
		String requestURI = ((VaadinServletRequest) vaadinRequest).getRequestURI();
		String fullURL = vaadinServletRequest.getRequestURL().toString();
		urlBase = fullURL.substring(0, fullURL.length() - requestURI.length());

		createBreadcrumb(layout);
		createFilesGrid(layout);

		layout.add(statusLabel);

		createButtonsLayout(layout);
	}

	private void createBreadcrumb(Div layout) {
		layout.add(breadcrumb);
		populateBreadcrumb();
	}

	private void populateBreadcrumb() {
		// pokud zjistím, že cesta neodpovídá, vyhodím 302 (přesměrování) na
		// aktuální polohu cílové kategorie
		List<BreadcrumbElement> breadcrumbElements = new ArrayList<>();
		for (FMItemTO c : explorer.getBreadcrumbChunks())
			breadcrumbElements
					.add(new BreadcrumbElement(c.getName(), getPageURL(fmPageFactory, c.getPathFromFMRoot())));
		breadcrumb.resetBreadcrumb(breadcrumbElements);
	}

	private void createFilesGrid(Div layout) {
		grid = new Grid<>();
		grid.setSelectionMode(SelectionMode.MULTI);
		grid.setColumnReorderingAllowed(true);
		grid.addThemeVariants(GridVariant.LUMO_COLUMN_BORDERS, GridVariant.LUMO_ROW_STRIPES, GridVariant.LUMO_COMPACT);
		grid.addClassName("top-margin");
		layout.add(grid);

		grid.addColumn(new IconRenderer<FMItemTO>(to -> {
			Image img = new Image(to.isDirectory() ? ImageIcon.FOLDER_16_ICON.createResource()
					: ImageIcon.DOCUMENT_16_ICON.createResource(), "");
			return img;
		}, to -> "")).setFlexGrow(0).setWidth("31px").setHeader("").setTextAlign(ColumnTextAlign.CENTER);

		Column<FMItemTO> nameColumn = grid.addColumn(FMItemTO::getName).setHeader("Název").setFlexGrow(100);
		grid.addColumn(FMItemTO::getSize).setHeader("Velikost").setTextAlign(ColumnTextAlign.END);
		grid.addColumn(new ComponentRenderer<Anchor, FMItemTO>(to -> {
			if (to.isDirectory()) {
				Anchor a = new Anchor();
				a.setVisible(false);
				return a;
			}
			String link = explorer.getDownloadLink(GrassPage.getContextPath(), to.getName());
			return new Anchor(link, "Stáhnout");
		})).setHeader("Stažení").setTextAlign(ColumnTextAlign.CENTER);

		grid.addColumn(new ComponentRenderer<Button, FMItemTO>(to -> {
			if (to.isDirectory()) {
				Button b = new Button();
				b.setVisible(false);
				return b;
			}
			String link = explorer.getDownloadLink(urlBase, to.getName());
			Button button = new Button("QR", e -> {
				WebDialog ww = new WebDialog("QR");
				Image image = new Image(new StreamResource(to.getName(), () -> {
					try {
						File file = QRCode.from(link).file();
						return new FileInputStream(file);
					} catch (IOException ex) {
						ex.printStackTrace();
						return null;
					}
				}), link);
				ww.addComponent(image);
				ww.open();
			});
			button.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);
			return button;
		})).setHeader("QR").setTextAlign(ColumnTextAlign.CENTER);

		grid.addColumn(new LocalDateTimeRenderer<>(FMItemTO::getLastModified, "d.MM.yyyy HH:mm")).setHeader("Upraveno");

		grid.addSelectionListener(e -> {
			Set<FMItemTO> value = e.getAllSelectedItems();
			statusLabel.setText(value.isEmpty() ? listFormatterValue : selectFormatter.format(value.size()));
		});

		grid.addItemClickListener(e -> {
			if (e.getClickCount() > 1)
				handleGridDblClick(e.getItem());
			else
				handleGridSingleClick(e.getItem(), e.isShiftKey());
		});

		HeaderRow filteringHeader = grid.appendHeaderRow();

		// Obsah
		TextField contentFilterField = new TextField();
		contentFilterField.addThemeVariants(TextFieldVariant.LUMO_SMALL);
		contentFilterField.setWidth("100%");
		contentFilterField.addValueChangeListener(e -> {
			filterName = e.getValue();
			populateGrid();
		});
		filteringHeader.getCell(nameColumn).setComponent(contentFilterField);

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
		int size = explorer.listCount(filterName);
		grid.setDataProvider(
				DataProvider.fromCallbacks(q -> explorer.listing(filterName, q.getOffset(), q.getLimit()), q -> size));
		listFormatterValue = listFormatter.format(size);
		statusLabel.setText(listFormatterValue);
	}

	private void createButtonsLayout(Div layout) {
		ButtonLayout buttonsLayout = new ButtonLayout();
		buttonsLayout.add(new CreateGridButton("Vytvořit nový adresář", e -> handleNewDirectory()));

		MultiFileMemoryBuffer buffer = new MultiFileMemoryBuffer();

		Upload upload = new Upload(buffer);
		upload.addClassName("top-margin");
		upload.addSucceededListener(event -> {
			switch (explorer.saveFile(buffer.getInputStream(event.getFileName()), event.getFileName())) {
			case SUCCESS:
				// refresh
				populateGrid();
				break;
			case ALREADY_EXISTS:
				UIUtils.showWarning("Soubor '" + event.getFileName()
						+ "' nebylo možné uložit - soubor s tímto názvem již existuje.");
				break;
			case NOT_VALID:
				UIUtils.showWarning("Soubor '" + event.getFileName()
						+ "' nebylo možné uložit - cílové umístění souboru se nachází mimo povolený rozsah souborů k prohlížení.");
				break;
			default:
				UIUtils.showWarning(
						"Soubor '" + event.getFileName() + "' nebylo možné uložit - došlo k systémové chybě.");
			}
		});
		buttonsLayout.add(upload);

		GridButton<FMItemTO> downloadButton = new GridButton<>("Stáhnout", this::handleDownloadAction, grid);
		downloadButton.setIcon(new Image(ImageIcon.DOWN_16_ICON.createResource(), "Stáhnout"));
		buttonsLayout.add(downloadButton);

		GridButton<FMItemTO> gotoButton = new GridButton<>("Přejít",
				items -> handleGotoDirFromCurrentDirAction(items.iterator().next()), grid);
		gotoButton.setIcon(new Image(ImageIcon.RIGHT_16_ICON.createResource(), "Přejít"));
		gotoButton.setEnableResolver(items -> items.size() == 1 && items.iterator().next().isDirectory());

		buttonsLayout.add(gotoButton);
		buttonsLayout.add(new ModifyGridButton<FMItemTO>("Přejmenovat", this::handleRenameAction, grid));
		buttonsLayout.add(new DeleteGridButton<FMItemTO>("Smazat", this::handleDeleteAction, grid));

		layout.add(buttonsLayout);
	}

	private void handleNewDirectory() {
		new FileNameDialog((s, w) -> {
			switch (explorer.createNewDir(s.getName())) {
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
		}).open();
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
		new FileNameDialog(item, (s, w) -> {
			switch (explorer.renameFile(item.getName(), s.getName())) {
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
		}).open();
	}

	private void handleDownloadAction(FMItemTO item) {
		String link = explorer.getDownloadLink(GrassPage.getContextPath(), item.getName());
		UI.getCurrent().getPage().open(link);
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
		// TODO
		// String currentURL =
		// explorer.getCurrentURL(getRequest().getContextRoot(),
		// fmPageFactory.getPageName());
		// Page.getCurrent().pushState(currentURL);
	}

}
