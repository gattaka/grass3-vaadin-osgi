package cz.gattserver.grass3.hw.ui.dialogs;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.Grid.Column;
import com.vaadin.flow.component.grid.Grid.SelectionMode;
import com.vaadin.flow.component.grid.GridSortOrder;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MultiFileMemoryBuffer;
import com.vaadin.flow.data.provider.SortDirection;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.renderer.IconRenderer;
import com.vaadin.flow.data.renderer.LocalDateRenderer;
import com.vaadin.flow.data.renderer.LocalDateTimeRenderer;
import com.vaadin.flow.data.renderer.TextRenderer;
import com.vaadin.flow.server.StreamResource;

import cz.gattserver.grass3.hw.HWConfiguration;
import cz.gattserver.grass3.hw.interfaces.HWItemFileTO;
import cz.gattserver.grass3.hw.interfaces.HWItemTO;
import cz.gattserver.grass3.hw.interfaces.ServiceNoteTO;
import cz.gattserver.grass3.hw.service.HWService;
import cz.gattserver.grass3.ui.components.button.CreateButton;
import cz.gattserver.grass3.ui.components.button.DeleteGridButton;
import cz.gattserver.grass3.ui.components.button.ModifyGridButton;
import cz.gattserver.grass3.ui.pages.template.GrassPage;
import cz.gattserver.grass3.ui.util.ButtonLayout;
import cz.gattserver.web.common.spring.SpringContextHelper;
import cz.gattserver.web.common.ui.window.ConfirmDialog;
import cz.gattserver.web.common.ui.window.ErrorDialog;

public class HWItemDetailsDialog extends Dialog {

	private static final long serialVersionUID = -6773027334692911384L;

	private static final Logger logger = LoggerFactory.getLogger(HWItemDetailsDialog.class);

	private static final String DEFAULT_NOTE_LABEL_VALUE = "- Zvolte servisní záznam -";
	private static final String STATE_BIND = "customState";
	private static final String DATE_BIND = "customDate";

	private transient HWService hwService;

	private Tabs tabs;
	private Tab infoTab;
	private Tab serviceNotesTab;
	private Tab photosTab;
	private Tab docsTab;

	private Div tabLayout;

	private HWItemTO hwItem;
	private Long hwItemId;

	private Grid<ServiceNoteTO> serviceNotesGrid;
	private Grid<HWItemFileTO> docsGrid;

	private Column<ServiceNoteTO> serviceDateColumn;

	public HWItemDetailsDialog(Long hwItemId) {
		this.hwItemId = hwItemId;
		this.hwItem = getHWService().getHWItem(hwItemId);

		setWidth("900px");

		Div nameDiv = new Div(new Text(hwItem.getName()));
		nameDiv.getStyle().set("font-size", "15px").set("margin-bottom", "var(--lumo-space-m)")
				.set("font-weight", "bold").set("margin-top", "calc(var(--lumo-space-m) / -2)");
		add(nameDiv);

		infoTab = new Tab("Info");
		serviceNotesTab = new Tab(createServiceNotesTabCaption());
		photosTab = new Tab(createPhotosTabCaption());
		docsTab = new Tab(createDocsTabCaption());

		tabs = new Tabs();
		tabs.add(infoTab, serviceNotesTab, photosTab, docsTab);
		add(tabs);

		tabLayout = new Div();
		tabLayout.setSizeFull();
		tabLayout.addClassName("top-margin");
		add(tabLayout);

		tabs.addSelectedChangeListener(e -> {
			switch (tabs.getSelectedIndex()) {
			default:
			case 0:
				switchInfoTab();
				break;
			case 1:
				switchServiceNotesTab();
				break;
			case 2:
				switchPhotosTab();
				break;
			case 3:
				switchDocsTab();
				break;
			}
		});

		switchInfoTab();
	}

	private String createServiceNotesTabCaption() {
		return "Záznamy (" + hwItem.getServiceNotes().size() + ")";
	}

	private String createPhotosTabCaption() {
		return "Fotografie (" + getHWService().getHWItemImagesFilesCount(hwItemId) + ")";
	}

	private String createDocsTabCaption() {
		return "Dokumentace (" + getHWService().getHWItemDocumentsFilesCount(hwItemId) + ")";
	}

	private HWService getHWService() {
		if (hwService == null)
			hwService = SpringContextHelper.getBean(HWService.class);
		return hwService;
	}

	public void switchInfoTab() {
		tabLayout.removeAll();
		tabLayout.add(new HWDetailsInfoTab(hwItem, this));
		tabs.setSelectedTab(infoTab);
	}

	private void populateServiceNotesGrid() {
		serviceNotesGrid.setItems(hwItem.getServiceNotes());
		serviceNotesGrid.sort(Arrays.asList(new GridSortOrder<>(serviceDateColumn, SortDirection.DESCENDING)));
	}

	private void switchServiceNotesTab() {
		tabLayout.removeAll();

		/**
		 * Tabulka záznamů
		 */
		serviceNotesGrid = new Grid<>(ServiceNoteTO.class);
		serviceNotesGrid.setSelectionMode(SelectionMode.SINGLE);
		serviceDateColumn = serviceNotesGrid
				.addColumn(new LocalDateRenderer<ServiceNoteTO>(ServiceNoteTO::getDate, "dd.MM.yyyy"))
				.setHeader("Datum").setKey(DATE_BIND).setTextAlign(ColumnTextAlign.END);
		serviceNotesGrid.addColumn(hw -> hw.getState().getName()).setHeader("Stav").setKey(STATE_BIND).setWidth("130px")
				.setFlexGrow(0);
		serviceNotesGrid.getColumnByKey("usedInName").setHeader("Je součástí").setWidth("180px").setFlexGrow(0);
		serviceNotesGrid.getColumnByKey("description").setHeader("Obsah");
		Column<ServiceNoteTO> idColumn = serviceNotesGrid.getColumnByKey("id");
		idColumn.setVisible(false);
		serviceNotesGrid.setColumns("id", DATE_BIND, STATE_BIND, "usedInName", "description");
		serviceNotesGrid.setWidth("100%");
		serviceNotesGrid.setHeight("200px");

		serviceNotesGrid
				.sort(Arrays.asList(new GridSortOrder<ServiceNoteTO>(serviceDateColumn, SortDirection.ASCENDING),
						new GridSortOrder<ServiceNoteTO>(idColumn, SortDirection.ASCENDING)));

		populateServiceNotesGrid();

		tabLayout.add(serviceNotesGrid);

		/**
		 * Detail záznamu
		 */
		final Div serviceNoteDescription = new Div();
		serviceNoteDescription.add(DEFAULT_NOTE_LABEL_VALUE);
		serviceNoteDescription.setWidth("100%");
		serviceNoteDescription.setHeight(null);
		serviceNoteDescription.addClassName("hw-panel");
		serviceNoteDescription.setSizeFull();
		tabLayout.add(serviceNoteDescription);

		/**
		 * Založení nového servisního záznamu
		 */
		Button newNoteBtn = new CreateButton(e -> new ServiceNoteCreateDialog(hwItem) {
			private static final long serialVersionUID = -5582822648042555576L;

			@Override
			protected void onSuccess(ServiceNoteTO noteDTO) {
				switchInfoTab();
				populateServiceNotesGrid();
				serviceNotesGrid.select(noteDTO);
				serviceNotesTab.setLabel(createServiceNotesTabCaption());
			}
		}.open());

		/**
		 * Úprava záznamu
		 */
		Button fixNoteBtn = new ModifyGridButton<>("Opravit záznam", event -> {
			if (serviceNotesGrid.getSelectedItems().isEmpty())
				return;
			new ServiceNoteCreateDialog(hwItem, serviceNotesGrid.getSelectedItems().iterator().next()) {
				private static final long serialVersionUID = -5582822648042555576L;

				@Override
				protected void onSuccess(ServiceNoteTO noteDTO) {
					populateServiceNotesGrid();
				}
			}.open();
		}, serviceNotesGrid);

		/**
		 * Smazání záznamu
		 */
		Button deleteNoteBtn = new DeleteGridButton<>("Smazat záznam", items -> {
			ServiceNoteTO item = items.iterator().next();
			getHWService().deleteServiceNote(item, hwItemId);
			hwItem.getServiceNotes().remove(item);
			populateServiceNotesGrid();
			serviceNotesTab.setLabel(createServiceNotesTabCaption());
		}, serviceNotesGrid);

		serviceNotesGrid.addSelectionListener(selection -> {
			boolean sthSelected = false;
			if (selection.getFirstSelectedItem().isPresent()) {
				sthSelected = true;
				ServiceNoteTO serviceNoteDTO = selection.getFirstSelectedItem().get();
				serviceNoteDescription.setText((String) serviceNoteDTO.getDescription());
			} else {
				serviceNoteDescription.setText(DEFAULT_NOTE_LABEL_VALUE);
			}
			fixNoteBtn.setEnabled(sthSelected);
			deleteNoteBtn.setEnabled(sthSelected);
		});

		HorizontalLayout operationsLayout = new HorizontalLayout();
		operationsLayout.setSpacing(true);
		tabLayout.add(operationsLayout);

		operationsLayout.add(newNoteBtn);
		operationsLayout.add(fixNoteBtn);
		operationsLayout.add(deleteNoteBtn);
	}

	private void switchPhotosTab() {
		tabLayout.removeAll();

		MultiFileMemoryBuffer buffer = new MultiFileMemoryBuffer();

		Upload upload = new Upload(buffer);
		upload.addClassName("top-margin");
		upload.setAcceptedFileTypes("image/jpeg", "image/png", "image/gif");
		upload.addSucceededListener(event -> {
			try {
				getHWService().saveImagesFile(buffer.getInputStream(event.getFileName()), event.getFileName(), hwItem);
				// refresh listu
				tabLayout.removeAll();
				createImagesList(upload);
				photosTab.setLabel(createPhotosTabCaption());
			} catch (IOException e) {
				String msg = "Nezdařilo se uložit obrázek";
				logger.error(msg, e);
				new ErrorDialog(msg).open();
			}
		});

		createImagesList(upload);
		tabLayout.add(upload);
	}

	private void createImagesList(Upload upload) {
		Grid<HWItemFileTO> grid = new Grid<>();
		List<HWItemFileTO> items = getHWService().getHWItemImagesFiles(hwItemId);
		grid.setItems(items);
		grid.setSizeFull();
		grid.addThemeVariants(GridVariant.LUMO_COLUMN_BORDERS, GridVariant.LUMO_ROW_STRIPES, GridVariant.LUMO_COMPACT);
		grid.addClassName("top-margin");
		grid.getStyle().set("height", "calc(100% - 85px)");

		tabLayout.add(grid);

		grid.addColumn(new IconRenderer<HWItemFileTO>(to -> {
			Image img = new Image(new StreamResource(to.getName(),
					() -> getHWService().getHWItemImagesFileInputStream(hwItemId, to.getName())), to.getName());
			img.addClassName("thumbnail-200");
			return img;
		}, c -> "")).setFlexGrow(0).setWidth("215px").setHeader("Náhled").setTextAlign(ColumnTextAlign.CENTER);

		grid.addColumn(new TextRenderer<>(to -> to.getName())).setHeader("Název").setFlexGrow(100);

		grid.addColumn(new ComponentRenderer<>(to -> {
			Button button = new Button("Detail", e -> UI.getCurrent().getPage()
					.open(HWConfiguration.HW_PATH + "/" + hwItem.getId() + "/img/" + to.getName()));
			button.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);
			return button;
		})).setHeader("Detail").setTextAlign(ColumnTextAlign.CENTER).setAutoWidth(true);

		grid.addColumn(new ComponentRenderer<>(to -> {
			Button button = new Button("Smazat", be -> {
				new ConfirmDialog("Opravdu smazat?", e -> {
					getHWService().deleteHWItemImagesFile(hwItemId, to.getName());
					tabLayout.removeAll();
					createImagesList(upload);
					tabLayout.add(upload);
					photosTab.setLabel(createPhotosTabCaption());
				}).open();
			});
			button.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);
			return button;
		})).setHeader("Smazat").setTextAlign(ColumnTextAlign.CENTER).setAutoWidth(true);
		grid.addColumn(new TextRenderer<>(HWItemFileTO::getSize)).setHeader("Velikost")
				.setTextAlign(ColumnTextAlign.END).setFlexGrow(0).setWidth("60px");
	}

	private void populateDocsGrid() {
		docsGrid.setItems(getHWService().getHWItemDocumentsFiles(hwItemId));
		docsGrid.getDataProvider().refreshAll();
		docsGrid.deselectAll();
	}

	private void switchDocsTab() {
		tabLayout.removeAll();

		docsGrid = new Grid<>(HWItemFileTO.class);
		docsGrid.addColumn(HWItemFileTO::getName).setHeader("Název");
		docsGrid.addColumn(HWItemFileTO::getSize).setHeader("Velikost").setTextAlign(ColumnTextAlign.END);
		docsGrid.addColumn(new LocalDateTimeRenderer<HWItemFileTO>(HWItemFileTO::getLastModified, "d.MM.yyyy HH:mm"))
				.setKey("datum").setHeader("Datum");
		docsGrid.setSizeFull();
		tabLayout.add(docsGrid);

		populateDocsGrid();

		MultiFileMemoryBuffer buffer = new MultiFileMemoryBuffer();

		Upload upload = new Upload(buffer);
		upload.addClassName("top-margin");
		upload.addSucceededListener(event -> {
			try {
				getHWService().saveDocumentsFile(buffer.getInputStream(event.getFileName()), event.getFileName(),
						hwItemId);

				// refresh listu
				populateDocsGrid();
				docsTab.setLabel(createDocsTabCaption());
			} catch (IOException e) {
				String msg = "Nezdařilo se uložit soubor";
				logger.error(msg, e);
				new ErrorDialog(msg).open();
			}
		});

		tabLayout.add(upload);

		ButtonLayout buttonLayout = new ButtonLayout();
		tabLayout.add(buttonLayout);

		// TODO
		// final Button hwItemDocumentDownloadBtn = new ImageButton("Stáhnout",
		// ImageIcon.DOWN_16_ICON.createResource());
		// buttonLayout.add(hwItemDocumentDownloadBtn);
		// hwItemDocumentDownloadBtn.setEnabled(false);

		final Button hwItemDocumentDeleteBtn = new DeleteGridButton<>("Smazat", items -> {
			HWItemFileTO item = items.iterator().next();
			getHWService().deleteHWItemDocumentsFile(hwItemId, item.getName());
			populateDocsGrid();
			docsTab.setLabel(createDocsTabCaption());
		}, docsGrid);
		buttonLayout.add(hwItemDocumentDeleteBtn);

		docsGrid.addItemClickListener(e -> {
			if (e.getClickCount() > 1)
				downloadDocument(e.getItem());
		});

		// TODO
		// docsGrid.addSelectionListener(selection -> {
		// if (downloadBtnRegistration != null)
		// downloadBtnRegistration.remove();
		// if (selection.getFirstSelectedItem().isPresent()) {
		// HWItemFileTO item = selection.getFirstSelectedItem().get();
		// hwItemDocumentDeleteBtn.setEnabled(true);
		// hwItemDocumentDownloadBtn.setEnabled(true);
		// downloadBtnRegistration =
		// hwItemDocumentDownloadBtn.addClickListener(e ->
		// downloadDocument(item));
		// } else {
		// hwItemDocumentDownloadBtn.setEnabled(false);
		// hwItemDocumentDeleteBtn.setEnabled(false);
		// }
		// });
	}

	private void downloadDocument(HWItemFileTO item) {
		UI.getCurrent().getPage().executeJs("window.open('" + GrassPage.getContextPath() + "/" + HWConfiguration.HW_PATH
				+ "/" + hwItemId + "/doc/" + item.getName() + "', '_blank');");
	}

}
