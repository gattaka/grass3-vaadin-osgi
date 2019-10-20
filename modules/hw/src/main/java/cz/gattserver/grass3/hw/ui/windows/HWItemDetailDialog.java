package cz.gattserver.grass3.hw.ui.windows;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

import javax.imageio.ImageIO;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.Grid.Column;
import com.vaadin.flow.component.grid.Grid.SelectionMode;
import com.vaadin.flow.component.grid.GridSortOrder;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.component.upload.receivers.MultiFileMemoryBuffer;
import com.vaadin.flow.data.provider.SortDirection;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.renderer.IconRenderer;
import com.vaadin.flow.data.renderer.LocalDateRenderer;
import com.vaadin.flow.data.renderer.LocalDateTimeRenderer;
import com.vaadin.flow.data.renderer.TextRenderer;
import com.vaadin.flow.server.StreamResource;

import cz.gattserver.common.util.CZAmountFormatter;
import cz.gattserver.common.util.MoneyFormatter;
import cz.gattserver.grass3.exception.GrassException;
import cz.gattserver.grass3.hw.HWConfiguration;
import cz.gattserver.grass3.hw.interfaces.HWItemFileTO;
import cz.gattserver.grass3.hw.interfaces.HWItemOverviewTO;
import cz.gattserver.grass3.hw.interfaces.HWItemTO;
import cz.gattserver.grass3.hw.interfaces.ServiceNoteTO;
import cz.gattserver.grass3.hw.service.HWService;
import cz.gattserver.grass3.ui.components.button.CreateButton;
import cz.gattserver.grass3.ui.components.button.DeleteButton;
import cz.gattserver.grass3.ui.components.button.DeleteGridButton;
import cz.gattserver.grass3.ui.components.button.ModifyButton;
import cz.gattserver.grass3.ui.components.button.ModifyGridButton;
import cz.gattserver.grass3.ui.pages.template.GrassPage;
import cz.gattserver.grass3.ui.util.ButtonLayout;
import cz.gattserver.grass3.ui.util.UIUtils;
import cz.gattserver.web.common.spring.SpringContextHelper;
import cz.gattserver.web.common.ui.ImageIcon;
import cz.gattserver.web.common.ui.Strong;
import cz.gattserver.web.common.ui.window.ConfirmDialog;
import cz.gattserver.web.common.ui.window.ErrorDialog;
import cz.gattserver.web.common.ui.window.ImageDetailWindow;
import cz.gattserver.web.common.ui.window.WebDialog;

public class HWItemDetailDialog extends WebDialog {

	private static final long serialVersionUID = -6773027334692911384L;

	private static final Logger logger = LoggerFactory.getLogger(HWItemDetailDialog.class);

	private static final String DEFAULT_NOTE_LABEL_VALUE = "- Zvolte servisní záznam -";
	private static final String BORDERED_STYLE = "bordered";
	private static final String STATE_BIND = "customState";
	private static final String DATE_BIND = "customDate";

	private transient HWService hwService;

	private VerticalLayout hwImageLayout;

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

	private ChangeListener changeListener;

	private Column<ServiceNoteTO> serviceDateColumn;

	public HWItemDetailDialog(Long hwItemId) {
		this.hwItemId = hwItemId;
		this.hwItem = getHWService().getHWItem(hwItemId);

		setWidth("900px");
		setHeight("700px");

		infoTab = new Tab("Info");
		serviceNotesTab = new Tab(createServiceNotesTabCaption());
		photosTab = new Tab(createPhotosTabCaption());
		docsTab = new Tab(createDocsTabCaption());

		tabs = new Tabs();
		tabs.setSizeFull();
		tabs.add(infoTab, serviceNotesTab, photosTab, docsTab);
		add(tabs);

		tabs.addSelectedChangeListener(e -> {
			switch (tabs.getSelectedIndex()) {
			default:
			case 0:
				createDetailsTab();
				break;
			case 1:
				createServiceNotesTab();
				break;
			case 2:
				createPhotosTab();
				break;
			case 3:
				createDocsTab();
				break;
			}
		});
		
		createDetailsTab();
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

	private String createPriceString(BigDecimal price) {
		if (price == null)
			return "-";
		return MoneyFormatter.format(price);
	}

	private String createWarrantyYearsString(Integer warrantyYears) {
		return new CZAmountFormatter("rok", "roky", "let").format(warrantyYears);
	}

	/**
	 * Pokusí se získat ikonu HW
	 */
	private boolean tryCreateHWImage(final HWItemTO hwItem) {
		InputStream iconIs;
		iconIs = getHWService().getHWItemIconFileInputStream(hwItemId);
		if (iconIs == null)
			return false;

		hwImageLayout.removeAll();

		// musí se jmenovat s příponou, aby se vůbec zobrazil
		Image image = new Image(new StreamResource("icon", () -> iconIs), "icon");
		image.addClassName("thumbnail-200");

		hwImageLayout.add(image);

		HorizontalLayout btnLayout = new HorizontalLayout();
		btnLayout.setSpacing(true);
		btnLayout.setPadding(false);

		Button hwItemImageDetailBtn = new Button("Detail", e -> {
			BufferedImage bimg = null;
			InputStream is = getHWService().getHWItemIconFileInputStream(hwItemId);
			if (is != null)
				try {
					bimg = ImageIO.read(is);
					int width = bimg.getWidth();
					int height = bimg.getHeight();
					new ImageDetailWindow(hwItem.getName(), width, height, new StreamResource(hwItem.getName(),
							() -> getHWService().getHWItemIconFileInputStream(hwItemId))).open();
				} catch (IOException ex) {
					throw new GrassException("Při čtení souboru ikony HW položky došlo k chybě.", ex);
				}
		});
		hwItemImageDetailBtn.setIcon(new Image(ImageIcon.SEARCH_16_ICON.createResource(), "detail"));

		Button hwItemImageDeleteBtn = new DeleteButton(
				e -> new ConfirmDialog("Opravdu smazat foto HW položky ?", ev -> {
					getHWService().deleteHWItemIconFile(hwItemId);
					createHWItemImageUpload(hwItem);
				}).open());

		btnLayout.add(hwItemImageDetailBtn);
		btnLayout.add(hwItemImageDeleteBtn);

		hwImageLayout.add(btnLayout);
		return true;
	}

	/**
	 * Vytváří form pro vložení ikony HW
	 */
	private void createHWItemImageUpload(final HWItemTO hwItem) {
		MemoryBuffer buffer = new MemoryBuffer();
		Upload upload = new Upload(buffer);
		upload.setMaxFileSize(2000000);
		upload.setAcceptedFileTypes("image/jpg", "image/jpeg", "image/png");
		upload.addSucceededListener(e -> {
			try {
				// vytvoř miniaturu
				OutputStream bos = getHWService().createHWItemIconOutputStream(e.getFileName(), hwItemId);
				IOUtils.copy(buffer.getInputStream(), bos);
				tryCreateHWImage(hwItem);
			} catch (IOException ex) {
				String err = "Nezdařilo se nahrát obrázek nápoje";
				logger.error(err, ex);
				UIUtils.showError(err);
			}
		});

		HorizontalLayout uploadWrapperLayout = new HorizontalLayout();
		uploadWrapperLayout.setWidth("220px");
		uploadWrapperLayout.setHeight("220px");
		uploadWrapperLayout.add(upload);
		uploadWrapperLayout.setPadding(false);

		hwImageLayout.removeAll();
		hwImageLayout.add(uploadWrapperLayout);
	}

	private void createHWImageOrUpload(final HWItemTO hwItem) {
		if (!tryCreateHWImage(hwItem))
			createHWItemImageUpload(hwItem);
	}

	private void createDetailsTab() {
		tabLayout.removeAll();

		HorizontalLayout itemLayout = new HorizontalLayout();
		itemLayout.setSpacing(false);
		itemLayout.setPadding(false);
		itemLayout.setSizeUndefined();
		tabLayout.add(itemLayout);

		/**
		 * Foto
		 */
		hwImageLayout = new VerticalLayout();
		hwImageLayout.setSpacing(true);
		hwImageLayout.setPadding(false);
		itemLayout.add(hwImageLayout);
		createHWImageOrUpload(hwItem);

		/**
		 * Grid
		 */
		FormLayout winLayout = new FormLayout();
		itemLayout.add(winLayout);

		/**
		 * Typy
		 */
		HorizontalLayout tags = new HorizontalLayout();
		tags.setSpacing(true);
		hwItem.getTypes().forEach(typeName -> {
			Button token = new Button(typeName);
			tags.add(token);
		});
		winLayout.add(tags);

		DateTimeFormatter format = DateTimeFormatter.ofPattern("d.M.yyyy");

		winLayout.add(new Strong("<strong>Stav</strong>"));
		winLayout.add(new Strong("<strong>Získáno</strong>"));
		winLayout.add(new Strong("<strong>Spravováno pro</strong>"));

		winLayout.add(hwItem.getState().getName());

		String purchDate = hwItem.getPurchaseDate() == null ? "-" : hwItem.getPurchaseDate().format(format);
		winLayout.add(purchDate);

		winLayout.add(hwItem.getSupervizedFor() == null ? "-" : hwItem.getSupervizedFor());

		winLayout.add(new Strong("<strong>Cena</strong>"));
		winLayout.add(new Strong("<strong>Odepsáno</strong>"));
		winLayout.add(new Strong("<strong>Záruka</strong>"));

		winLayout.add(createPriceString(hwItem.getPrice()));

		String destrDate = hwItem.getDestructionDate() == null ? "-" : hwItem.getDestructionDate().format(format);
		winLayout.add(destrDate);

		HorizontalLayout zarukaLayout = new HorizontalLayout();
		zarukaLayout.setSpacing(true);
		if (hwItem.getWarrantyYears() != null && hwItem.getWarrantyYears() > 0 && hwItem.getPurchaseDate() != null) {
			LocalDate endDate = hwItem.getPurchaseDate().plusYears(hwItem.getWarrantyYears());
			boolean isInWarranty = endDate.isAfter(LocalDate.now());
			Image emb = new Image(
					isInWarranty ? ImageIcon.TICK_16_ICON.createResource() : ImageIcon.DELETE_16_ICON.createResource(),
					"warranty");
			zarukaLayout.add(emb);
			String zarukaContent = hwItem.getWarrantyYears() + " "
					+ createWarrantyYearsString(hwItem.getWarrantyYears()) + " (do " + endDate.format(format) + ")";
			zarukaLayout.add(zarukaContent);
		} else {
			zarukaLayout.add("");
		}
		winLayout.add(zarukaLayout);

		winLayout.add(new Strong("<strong>Je součástí</strong>"));
		if (hwItem.getUsedIn() == null) {
			winLayout.add("-");
		} else {
			Button usedInBtn = new Button(hwItem.getUsedIn().getName());
			usedInBtn.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);
			usedInBtn.addClickListener(e -> {
				close();
				new HWItemDetailDialog(hwItem.getUsedIn().getId()).open();
			});
			winLayout.add(usedInBtn);
		}

		VerticalLayout partsWrapperLayout = new VerticalLayout();
		winLayout.add(partsWrapperLayout);

		H3 name = new H3("Součásti");
		partsWrapperLayout.add(name);

		List<HWItemOverviewTO> parts = getHWService().getAllParts(hwItemId);
		VerticalLayout partsLayout = new VerticalLayout();
		partsLayout.setSpacing(false);
		partsLayout.setPadding(true);

		VerticalLayout partsPanel = new VerticalLayout(partsLayout);
		partsPanel.setSizeFull();
		partsWrapperLayout.add(partsPanel);

		for (final HWItemOverviewTO part : parts) {
			Button partDetailBtn = new Button(part.getName());
			partDetailBtn.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);
			partDetailBtn.addClickListener(e -> {
				close();
				HWItemTO detailTO = getHWService().getHWItem(part.getId());
				new HWItemDetailDialog(detailTO.getId()).open();
			});
			partsLayout.add(partDetailBtn);
		}

		HorizontalLayout operationsLayout = new HorizontalLayout();
		operationsLayout.setSpacing(true);
		tabLayout.add(operationsLayout);

		/**
		 * Oprava údajů existující položky HW
		 */
		final Button fixBtn = new ModifyButton(e -> new HWItemDialog(hwItem) {
			private static final long serialVersionUID = -1397391593801030584L;

			@Override
			protected void onSuccess(HWItemTO dto) {
				if (changeListener != null)
					changeListener.onChange();
				createDetailsTab();
				tabs.setSelectedIndex(0);
			}
		}.open());
		operationsLayout.add(fixBtn);

		/**
		 * Smazání položky HW
		 */
		final Button deleteBtn = new DeleteButton(e -> new ConfirmDialog(
				"Opravdu smazat '" + hwItem.getName() + "' (budou smazány i servisní záznamy a údaje u součástí) ?",
				ev -> {
					try {
						getHWService().deleteHWItem(hwItemId);
						if (changeListener != null)
							changeListener.onChange();
						HWItemDetailDialog.this.close();
					} catch (Exception ex) {
						new ErrorDialog("Nezdařilo se smazat vybranou položku").open();
					}
				}).open());
		operationsLayout.add(deleteBtn);
	}

	private void populateServiceNotesGrid() {
		serviceNotesGrid.setItems(hwItem.getServiceNotes());
		serviceNotesGrid.sort(Arrays.asList(new GridSortOrder<>(serviceDateColumn, SortDirection.DESCENDING)));
	}

	private void createServiceNotesTab() {
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
				if (changeListener != null)
					changeListener.onChange();
				createDetailsTab();
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

	private void createPhotosTab() {
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

	private void createDocsTab() {
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

	public ChangeListener getChangeListener() {
		return changeListener;
	}

	public HWItemDetailDialog setChangeListener(ChangeListener changeListener) {
		this.changeListener = changeListener;
		return this;
	}

}
