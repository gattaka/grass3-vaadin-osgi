package cz.gattserver.grass3.hw.ui.windows;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import javax.imageio.ImageIO;

import com.vaadin.server.Page;
import com.vaadin.server.Resource;
import com.vaadin.server.StreamResource;
import com.vaadin.shared.Registration;
import com.vaadin.shared.data.sort.SortDirection;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.Column;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.JavaScript;
import com.vaadin.ui.Label;
import com.vaadin.ui.Layout;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TabSheet.Tab;
import com.vaadin.ui.UI;
import com.vaadin.ui.Upload;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.renderers.LocalDateRenderer;
import com.vaadin.ui.renderers.LocalDateTimeRenderer;
import com.vaadin.ui.renderers.TextRenderer;
import com.vaadin.ui.themes.ValoTheme;

import cz.gattserver.common.util.CZAmountFormatter;
import cz.gattserver.common.util.MoneyFormatter;
import cz.gattserver.grass3.exception.GrassException;
import cz.gattserver.grass3.hw.interfaces.HWItemTO;
import cz.gattserver.grass3.hw.HWConfiguration;
import cz.gattserver.grass3.hw.interfaces.HWItemFileTO;
import cz.gattserver.grass3.hw.interfaces.HWItemOverviewTO;
import cz.gattserver.grass3.hw.interfaces.ServiceNoteTO;
import cz.gattserver.grass3.hw.service.HWService;
import cz.gattserver.grass3.server.GrassRequest;
import cz.gattserver.grass3.ui.components.CreateButton;
import cz.gattserver.grass3.ui.components.DeleteButton;
import cz.gattserver.grass3.ui.components.DeleteGridButton;
import cz.gattserver.grass3.ui.components.ModifyButton;
import cz.gattserver.grass3.ui.components.ModifyGridButton;
import cz.gattserver.grass3.ui.util.GridUtils;
import cz.gattserver.web.common.spring.SpringContextHelper;
import cz.gattserver.web.common.ui.ImageIcon;
import cz.gattserver.web.common.ui.MultiUpload;
import cz.gattserver.web.common.ui.window.ConfirmWindow;
import cz.gattserver.web.common.ui.window.ErrorWindow;
import cz.gattserver.web.common.ui.window.ImageDetailWindow;
import cz.gattserver.web.common.ui.window.WebWindow;

public class HWItemDetailWindow extends WebWindow {

	private static final long serialVersionUID = -6773027334692911384L;

	private static final String DEFAULT_NOTE_LABEL_VALUE = "- Zvolte servisní záznam -";
	private static final String BORDERED_STYLE = "bordered";
	private static final String STATE_BIND = "customState";
	private static final String DATE_BIND = "customDate";

	private transient HWService hwService;

	private VerticalLayout hwImageLayout;

	private Registration downloadBtnRegistration;

	private TabSheet sheet;
	private HWItemTO hwItem;
	private Long hwItemId;

	private Grid<ServiceNoteTO> serviceNotesGrid;
	private Grid<HWItemFileTO> docsGrid;

	private GrassRequest grassRequest;

	private ChangeListener changeListener;

	private Column<ServiceNoteTO, LocalDate> serviceDateColumn;

	public HWItemDetailWindow(Long hwItemId, GrassRequest grassRequest) {
		super("Detail HW");
		this.hwItemId = hwItemId;
		this.grassRequest = grassRequest;

		setWidth("900px");
		setHeight("700px");

		sheet = new TabSheet();
		sheet.setSizeFull();
		createFirstTab();
		sheet.addTab(createServiceNotesTab(), createServiceNotesTabCaption(),
				ImageIcon.CLIPBOARD_16_ICON.createResource());
		sheet.addTab(createPhotosTab(), createPhotosTabCaption(), ImageIcon.IMG_16_ICON.createResource());
		sheet.addTab(createDocsTab(), createDocsTabCaption(), ImageIcon.DOCUMENT_16_ICON.createResource());

		VerticalLayout layout = new VerticalLayout();
		layout.setSpacing(false);
		layout.setMargin(new MarginInfo(false, true, true, true));
		layout.setSizeFull();

		Label name = new Label("<h3>" + hwItem.getName() + "</h3>", ContentMode.HTML);
		layout.addComponent(name);
		layout.addComponent(sheet);
		layout.setExpandRatio(sheet, 1);
		setContent(layout);

		center();
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

	private Label createShiftedLabel(String caption) {
		Label label = new Label(caption, ContentMode.HTML);
		label.addStyleName("shiftlabel");
		return label;
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

		hwImageLayout.removeAllComponents();

		// musí se jmenovat s příponou, aby se vůbec zobrazil
		final Resource resource = new StreamResource(() -> iconIs, "icon.jpg");
		Embedded hwItemImage = new Embedded(null, resource);
		hwItemImage.addStyleName("thumbnail-200");

		hwImageLayout.addComponent(hwItemImage);
		hwImageLayout.setComponentAlignment(hwItemImage, Alignment.TOP_CENTER);

		HorizontalLayout btnLayout = new HorizontalLayout();
		btnLayout.setSpacing(true);
		btnLayout.setMargin(false);

		Button hwItemImageDetailBtn = new Button("Detail", e -> {
			BufferedImage bimg = null;
			InputStream is = getHWService().getHWItemIconFileInputStream(hwItemId);
			if (is != null)
				try {
					bimg = ImageIO.read(is);
					int width = bimg.getWidth();
					int height = bimg.getHeight();
					UI.getCurrent().addWindow(new ImageDetailWindow(hwItem.getName(), width, height, new StreamResource(
							() -> getHWService().getHWItemIconFileInputStream(hwItemId), hwItem.getName())));
				} catch (IOException ex) {
					throw new GrassException("Při čtení souboru ikony HW položky došlo k chybě.", ex);
				}
		});
		hwItemImageDetailBtn.setIcon(ImageIcon.SEARCH_16_ICON.createResource());

		Button hwItemImageDeleteBtn = new DeleteButton(
				e -> UI.getCurrent().addWindow(new ConfirmWindow("Opravdu smazat foto HW položky ?", ev -> {
					getHWService().deleteHWItemIconFile(hwItemId);
					createHWItemImageUpload(hwItem);
				})));

		btnLayout.addComponent(hwItemImageDetailBtn);
		btnLayout.addComponent(hwItemImageDeleteBtn);

		hwImageLayout.addComponent(btnLayout);
		hwImageLayout.setComponentAlignment(btnLayout, Alignment.BOTTOM_CENTER);
		return true;
	}

	/**
	 * Vytváří form pro vložení ikony HW
	 */
	private void createHWItemImageUpload(final HWItemTO hwItem) {
		Upload upload = new Upload(null,
				(String filename, String mimeType) -> getHWService().createHWItemIconOutputStream(filename, hwItemId));
		upload.addSucceededListener(e -> tryCreateHWImage(hwItem));
		upload.setButtonCaption("Vložit foto");

		HorizontalLayout uploadWrapperLayout = new HorizontalLayout();
		uploadWrapperLayout.addStyleName(BORDERED_STYLE);
		uploadWrapperLayout.setWidth("220px");
		uploadWrapperLayout.setHeight("220px");
		uploadWrapperLayout.addComponent(upload);
		uploadWrapperLayout.setMargin(false);
		uploadWrapperLayout.setComponentAlignment(upload, Alignment.MIDDLE_CENTER);

		hwImageLayout.removeAllComponents();
		hwImageLayout.addComponent(uploadWrapperLayout);
		hwImageLayout.setComponentAlignment(uploadWrapperLayout, Alignment.MIDDLE_CENTER);
	}

	private void createHWImageOrUpload(final HWItemTO hwItem) {
		if (!tryCreateHWImage(hwItem))
			createHWItemImageUpload(hwItem);
	}

	private VerticalLayout createWrapperLayout() {
		VerticalLayout wrapperLayout = new VerticalLayout();
		wrapperLayout.setSpacing(true);
		wrapperLayout.setMargin(new MarginInfo(true, false, false, false));
		wrapperLayout.setSizeFull();
		return wrapperLayout;
	}

	private Layout createItemDetailsLayout(HWItemTO hwItem) {

		VerticalLayout wrapperLayout = createWrapperLayout();

		HorizontalLayout itemLayout = new HorizontalLayout();
		itemLayout.setSpacing(false);
		itemLayout.setMargin(false);
		itemLayout.setSizeUndefined();
		wrapperLayout.addComponent(itemLayout);

		/**
		 * Foto
		 */
		hwImageLayout = new VerticalLayout();
		hwImageLayout.setSpacing(true);
		hwImageLayout.setMargin(false);
		itemLayout.addComponent(hwImageLayout);
		createHWImageOrUpload(hwItem);

		/**
		 * Grid
		 */
		GridLayout winLayout = new GridLayout(5, 7);
		itemLayout.addComponent(winLayout);
		itemLayout.setComponentAlignment(winLayout, Alignment.TOP_LEFT);
		winLayout.setSpacing(true);
		winLayout.setMargin(false);

		/**
		 * Typy
		 */
		HorizontalLayout tags = new HorizontalLayout();
		tags.setSpacing(true);
		hwItem.getTypes().forEach(typeName -> {
			Label token = new Label(typeName);
			token.setSizeUndefined();
			token.setStyleName("read-only-token");
			tags.addComponent(token);
		});
		winLayout.addComponent(tags, 1, 0, 3, 0);

		DateTimeFormatter format = DateTimeFormatter.ofPattern("d.M.yyyy");

		winLayout.addComponent(new Label("<strong>Stav</strong>", ContentMode.HTML), 1, 1);
		winLayout.getComponent(1, 1).setWidth("80px");
		winLayout.addComponent(createShiftedLabel(hwItem.getState().getName()), 1, 2);

		winLayout.addComponent(new Label("<strong>Získáno</strong>", ContentMode.HTML), 2, 1);
		winLayout.getComponent(2, 1).setWidth("80px");
		String purchDate = hwItem.getPurchaseDate() == null ? "-" : hwItem.getPurchaseDate().format(format);
		winLayout.addComponent(createShiftedLabel(purchDate), 2, 2);

		winLayout.addComponent(new Label("<strong>Spravováno pro</strong>", ContentMode.HTML), 3, 1);
		winLayout.addComponent(createShiftedLabel(hwItem.getSupervizedFor() == null ? "-" : hwItem.getSupervizedFor()),
				3, 2);

		winLayout.addComponent(new Label("<strong>Cena</strong>", ContentMode.HTML), 1, 3);
		winLayout.addComponent(createShiftedLabel(createPriceString(hwItem.getPrice())), 1, 4);

		winLayout.addComponent(new Label("<strong>Odepsáno</strong>", ContentMode.HTML), 2, 3);
		String destrDate = hwItem.getDestructionDate() == null ? "-" : hwItem.getDestructionDate().format(format);
		winLayout.addComponent(createShiftedLabel(destrDate), 2, 4);

		winLayout.addComponent(new Label("<strong>Záruka</strong>", ContentMode.HTML), 3, 3);
		HorizontalLayout zarukaLayout = new HorizontalLayout();
		zarukaLayout.setSpacing(true);
		if (hwItem.getWarrantyYears() != null && hwItem.getWarrantyYears() > 0 && hwItem.getPurchaseDate() != null) {
			LocalDate endDate = hwItem.getPurchaseDate().plusYears(hwItem.getWarrantyYears());
			boolean isInWarranty = endDate.isAfter(LocalDate.now());
			Embedded emb = new Embedded(null,
					isInWarranty ? ImageIcon.TICK_16_ICON.createResource() : ImageIcon.DELETE_16_ICON.createResource());
			zarukaLayout.addComponent(emb);
			String zarukaContent = hwItem.getWarrantyYears() + " "
					+ createWarrantyYearsString(hwItem.getWarrantyYears()) + " (do " + endDate.format(format) + ")";
			zarukaLayout.addComponent(new Label(zarukaContent));
		} else {
			zarukaLayout.addComponent(createShiftedLabel(""));
		}
		winLayout.addComponent(zarukaLayout, 3, 4);

		winLayout.addComponent(new Label("<strong>Je součástí</strong>", ContentMode.HTML), 1, 5);
		if (hwItem.getUsedIn() == null) {
			winLayout.addComponent(createShiftedLabel("-"), 1, 6, 4, 6);
		} else {
			Button usedInBtn = new Button(hwItem.getUsedIn().getName());
			usedInBtn.setDescription(hwItem.getUsedIn().getName());
			usedInBtn.setStyleName(ValoTheme.BUTTON_LINK);
			usedInBtn.addClickListener(e -> {
				close();
				UI.getCurrent().addWindow(new HWItemDetailWindow(hwItem.getUsedIn().getId(), grassRequest));
			});
			winLayout.addComponent(usedInBtn, 1, 6, 4, 6);
		}

		VerticalLayout partsWrapperLayout = new VerticalLayout();
		partsWrapperLayout.setSpacing(false);
		partsWrapperLayout.setMargin(false);
		partsWrapperLayout.setSizeFull();
		wrapperLayout.addComponent(partsWrapperLayout);
		wrapperLayout.setComponentAlignment(partsWrapperLayout, Alignment.TOP_LEFT);
		wrapperLayout.setExpandRatio(partsWrapperLayout, 1);

		Label name = new Label("<h3>Součásti</h3>", ContentMode.HTML);
		partsWrapperLayout.addComponent(name);

		List<HWItemOverviewTO> parts = getHWService().getAllParts(hwItemId);
		VerticalLayout partsLayout = new VerticalLayout();
		partsLayout.setSpacing(false);
		partsLayout.setMargin(true);
		Panel partsPanel = new Panel(partsLayout);
		partsPanel.setSizeFull();
		partsWrapperLayout.addComponent(partsPanel);
		partsWrapperLayout.setExpandRatio(partsPanel, 1);

		for (final HWItemOverviewTO part : parts) {
			Button partDetailBtn = new Button(part.getName());
			partDetailBtn.setDescription(part.getName());
			partDetailBtn.setStyleName(ValoTheme.BUTTON_LINK);
			partDetailBtn.addClickListener(e -> {
				close();
				HWItemTO detailTO = getHWService().getHWItem(part.getId());
				UI.getCurrent().addWindow(new HWItemDetailWindow(detailTO.getId(), grassRequest));
			});
			partsLayout.addComponent(partDetailBtn);
		}

		HorizontalLayout operationsLayout = new HorizontalLayout();
		operationsLayout.setSpacing(true);
		wrapperLayout.addComponent(operationsLayout);

		/**
		 * Oprava údajů existující položky HW
		 */
		final Button fixBtn = new ModifyButton(e -> UI.getCurrent().addWindow(new HWItemWindow(hwItem) {
			private static final long serialVersionUID = -1397391593801030584L;

			@Override
			protected void onSuccess(HWItemTO dto) {
				if (changeListener != null)
					changeListener.onChange();
				createFirstTab();
				sheet.setSelectedTab(0);
			}
		}));
		operationsLayout.addComponent(fixBtn);

		/**
		 * Smazání položky HW
		 */
		final Button deleteBtn = new DeleteButton(e -> UI.getCurrent().addWindow(new ConfirmWindow(
				"Opravdu smazat '" + hwItem.getName() + "' (budou smazány i servisní záznamy a údaje u součástí) ?",
				ev -> {
					try {
						getHWService().deleteHWItem(hwItemId);
						if (changeListener != null)
							changeListener.onChange();
						HWItemDetailWindow.this.close();
					} catch (Exception ex) {
						UI.getCurrent().addWindow(new ErrorWindow("Nezdařilo se smazat vybranou položku"));
					}
				})));
		operationsLayout.addComponent(deleteBtn);

		return wrapperLayout;
	}

	private void populateServiceNotesGrid() {
		serviceNotesGrid.setItems(hwItem.getServiceNotes());
		serviceNotesGrid.sort(serviceDateColumn, SortDirection.DESCENDING);
	}

	private Layout createServiceNotesTab() {
		VerticalLayout wrapperLayout = createWrapperLayout();

		/**
		 * Tabulka záznamů
		 */
		serviceNotesGrid = new Grid<>(ServiceNoteTO.class);
		serviceNotesGrid.setSelectionMode(SelectionMode.SINGLE);
		serviceDateColumn = serviceNotesGrid.addColumn(ServiceNoteTO::getDate, new LocalDateRenderer("dd.MM.yyyy"))
				.setCaption("Datum").setId(DATE_BIND).setStyleGenerator(item -> "v-align-right")
				.setWidth(GridUtils.DATE_COLUMN_WIDTH);
		serviceNotesGrid.addColumn(hw -> hw.getState().getName(), new TextRenderer()).setCaption("Stav")
				.setId(STATE_BIND).setWidth(130);
		serviceNotesGrid.getColumn("usedInName").setCaption("Je součástí").setWidth(180);
		serviceNotesGrid.getColumn("description").setCaption("Obsah");
		serviceNotesGrid.getColumn("id").setHidden(true); // jinak nepůjde sort
		serviceNotesGrid.setColumns("id", DATE_BIND, STATE_BIND, "usedInName", "description");
		serviceNotesGrid.setWidth("100%");
		serviceNotesGrid.setHeight("200px");

		serviceNotesGrid.sort(DATE_BIND);
		serviceNotesGrid.sort("id");

		populateServiceNotesGrid();

		wrapperLayout.addComponent(serviceNotesGrid);

		/**
		 * Detail záznamu
		 */
		final Label serviceNoteDescription = new Label(DEFAULT_NOTE_LABEL_VALUE);
		serviceNoteDescription.setWidth("100%");
		serviceNoteDescription.setHeight(null);
		Panel serviceNoteDetailPanel = new Panel(serviceNoteDescription);
		serviceNoteDetailPanel.setStyleName("hw-panel");
		serviceNoteDetailPanel.setSizeFull();
		wrapperLayout.addComponent(serviceNoteDetailPanel);
		wrapperLayout.setExpandRatio(serviceNoteDetailPanel, 1);

		/**
		 * Založení nového servisního záznamu
		 */
		Button newNoteBtn = new CreateButton(e -> UI.getCurrent().addWindow(new ServiceNoteCreateWindow(hwItem) {
			private static final long serialVersionUID = -5582822648042555576L;

			@Override
			protected void onSuccess(ServiceNoteTO noteDTO) {
				if (changeListener != null)
					changeListener.onChange();
				createFirstTab();
				populateServiceNotesGrid();
				serviceNotesGrid.select(noteDTO);
				sheet.getTab(1).setCaption(createServiceNotesTabCaption());
			}
		}));

		/**
		 * Úprava záznamu
		 */
		Button fixNoteBtn = new ModifyGridButton<>("Opravit záznam", event -> {
			if (serviceNotesGrid.getSelectedItems().isEmpty())
				return;
			UI.getCurrent().addWindow(
					new ServiceNoteCreateWindow(hwItem, serviceNotesGrid.getSelectedItems().iterator().next()) {
						private static final long serialVersionUID = -5582822648042555576L;

						@Override
						protected void onSuccess(ServiceNoteTO noteDTO) {
							populateServiceNotesGrid();
						}
					});
		}, serviceNotesGrid);

		/**
		 * Smazání záznamu
		 */
		Button deleteNoteBtn = new DeleteGridButton<>("Smazat záznam", items -> {
			ServiceNoteTO item = items.iterator().next();
			getHWService().deleteServiceNote(item, hwItemId);
			hwItem.getServiceNotes().remove(item);
			populateServiceNotesGrid();
			sheet.getTab(1).setCaption(createServiceNotesTabCaption());
		}, serviceNotesGrid);

		serviceNotesGrid.addSelectionListener(selection -> {
			boolean sthSelected = false;
			if (selection.getFirstSelectedItem().isPresent()) {
				sthSelected = true;
				ServiceNoteTO serviceNoteDTO = selection.getFirstSelectedItem().get();
				serviceNoteDescription.setValue((String) serviceNoteDTO.getDescription());
			} else {
				serviceNoteDescription.setValue(DEFAULT_NOTE_LABEL_VALUE);
			}
			fixNoteBtn.setEnabled(sthSelected);
			deleteNoteBtn.setEnabled(sthSelected);
		});

		HorizontalLayout operationsLayout = new HorizontalLayout();
		operationsLayout.setSpacing(true);
		wrapperLayout.addComponent(operationsLayout);

		operationsLayout.addComponent(newNoteBtn);
		operationsLayout.addComponent(fixNoteBtn);
		operationsLayout.addComponent(deleteNoteBtn);

		return wrapperLayout;
	}

	private Layout createPhotosTab() {
		VerticalLayout wrapperLayout = createWrapperLayout();

		GridLayout listLayout = new GridLayout();
		listLayout.setColumns(4);
		listLayout.setSpacing(true);
		listLayout.setMargin(true);

		Panel panel = new Panel(listLayout);
		panel.setSizeFull();
		wrapperLayout.addComponent(panel);
		wrapperLayout.setExpandRatio(panel, 1);

		createImagesList(listLayout);

		MultiUpload multiFileUpload = new MultiUpload() {
			private static final long serialVersionUID = -3899558855555370125L;

			@Override
			protected void fileUploadFinished(InputStream in, String fileName, String mimeType, long length,
					int filesLeftInQueue) {
				getHWService().saveImagesFile(in, fileName, hwItem);
				// refresh listu
				listLayout.removeAllComponents();
				createImagesList(listLayout);
				sheet.getTab(2).setCaption(createPhotosTabCaption());
			}
		};

		multiFileUpload.setCaption("Vložit fotografie");
		multiFileUpload.setSizeUndefined();
		wrapperLayout.addComponent(multiFileUpload);

		return wrapperLayout;
	}

	private void createImagesList(GridLayout listLayout) {
		for (final HWItemFileTO file : getHWService().getHWItemImagesFiles(hwItemId)) {

			VerticalLayout imageLayout = new VerticalLayout();
			listLayout.addComponent(imageLayout);
			listLayout.setComponentAlignment(imageLayout, Alignment.MIDDLE_CENTER);
			imageLayout.setSpacing(true);
			imageLayout.setMargin(false);

			Resource resource = new StreamResource(
					() -> getHWService().getHWItemImagesFileInputStream(hwItemId, file.getName()), file.getName());
			Embedded img = new Embedded(null, resource);
			img.addStyleName("thumbnail-200");
			imageLayout.addComponent(img);

			HorizontalLayout btnLayout = new HorizontalLayout();
			btnLayout.setSpacing(true);

			Button hwItemImageDetailBtn = new Button("Detail", e -> Page.getCurrent()
					.open(HWConfiguration.HW_PATH + "/" + hwItem.getId() + "/" + file.getName(), file.getName()));

			Button hwItemImageDeleteBtn = new DeleteButton(
					e -> UI.getCurrent().addWindow(new ConfirmWindow("Opravdu smazat foto HW položky ?", ev -> {
						getHWService().deleteHWItemImagesFile(hwItemId, file.getName());

						// refresh listu
						listLayout.removeAllComponents();
						createImagesList(listLayout);
						sheet.getTab(2).setCaption(createPhotosTabCaption());
					})));

			hwItemImageDetailBtn.setIcon(ImageIcon.SEARCH_16_ICON.createResource());
			hwItemImageDeleteBtn.setIcon(ImageIcon.DELETE_16_ICON.createResource());

			btnLayout.addComponent(hwItemImageDetailBtn);
			btnLayout.addComponent(hwItemImageDeleteBtn);

			imageLayout.addComponent(btnLayout);
			imageLayout.setComponentAlignment(btnLayout, Alignment.BOTTOM_CENTER);
		}
	}

	private void populateDocsGrid() {
		docsGrid.setItems(getHWService().getHWItemDocumentsFiles(hwItemId));
		docsGrid.getDataProvider().refreshAll();
		docsGrid.deselectAll();
	}

	private Layout createDocsTab() {
		VerticalLayout wrapperLayout = createWrapperLayout();

		docsGrid = new Grid<>(HWItemFileTO.class);
		docsGrid.addColumn(HWItemFileTO::getName).setCaption("Název");
		docsGrid.addColumn(HWItemFileTO::getSize, new TextRenderer()).setCaption("Velikost")
				.setStyleGenerator(item -> "v-align-right");
		docsGrid.addColumn(HWItemFileTO::getLastModified, new LocalDateTimeRenderer("d.MM.yyyy HH:mm")).setId("datum")
				.setCaption("Datum");
		docsGrid.setSizeFull();
		wrapperLayout.addComponent(docsGrid);
		wrapperLayout.setExpandRatio(docsGrid, 1);

		populateDocsGrid();

		HorizontalLayout uploadWrapperLayout = new HorizontalLayout();
		wrapperLayout.addComponent(uploadWrapperLayout);

		final MultiUpload multiFileUpload = new MultiUpload() {
			private static final long serialVersionUID = 8500364606014524121L;

			@Override
			public void fileUploadFinished(InputStream in, String fileName, String mime, long size,
					int filesLeftInQueue) {
				getHWService().saveDocumentsFile(in, fileName, hwItemId);

				// refresh listu
				populateDocsGrid();
				sheet.getTab(3).setCaption(createDocsTabCaption());
			}

		};
		multiFileUpload.setCaption("Vložit dokumenty");
		multiFileUpload.setSizeUndefined();
		uploadWrapperLayout.addComponent(multiFileUpload);

		final Button hwItemDocumentDownloadBtn = new Button("Stáhnout", ImageIcon.DOWN_16_ICON.createResource());
		uploadWrapperLayout.addComponent(hwItemDocumentDownloadBtn);
		hwItemDocumentDownloadBtn.setEnabled(false);

		final Button hwItemDocumentDeleteBtn = new DeleteGridButton<>("Smazat", items -> {
			HWItemFileTO item = items.iterator().next();
			getHWService().deleteHWItemDocumentsFile(hwItemId, item.getName());
			populateDocsGrid();
			sheet.getTab(3).setCaption(createDocsTabCaption());
		}, docsGrid);
		uploadWrapperLayout.addComponent(hwItemDocumentDeleteBtn);

		docsGrid.addItemClickListener(e -> {
			if (e.getMouseEventDetails().isDoubleClick())
				downloadDocument(e.getItem());
		});

		docsGrid.addSelectionListener(selection -> {
			if (downloadBtnRegistration != null)
				downloadBtnRegistration.remove();
			if (selection.getFirstSelectedItem().isPresent()) {
				HWItemFileTO item = selection.getFirstSelectedItem().get();
				hwItemDocumentDeleteBtn.setEnabled(true);
				hwItemDocumentDownloadBtn.setEnabled(true);
				downloadBtnRegistration = hwItemDocumentDownloadBtn.addClickListener(e -> downloadDocument(item));
			} else {
				hwItemDocumentDownloadBtn.setEnabled(false);
				hwItemDocumentDeleteBtn.setEnabled(false);
			}
		});

		return wrapperLayout;
	}

	private void downloadDocument(HWItemFileTO item) {
		JavaScript.eval("window.open('" + grassRequest.getContextRoot() + "/" + HWConfiguration.HW_PATH + "/" + hwItemId
				+ "/doc/" + item.getName() + "', '_blank');");
	}

	private void createFirstTab() {
		this.hwItem = getHWService().getHWItem(hwItemId);
		Tab tab = sheet.getTab(0);
		if (tab != null)
			sheet.removeTab(tab);
		sheet.addTab(createItemDetailsLayout(hwItem), "Info", ImageIcon.GEAR2_16_ICON.createResource(), 0);
	}

	public ChangeListener getChangeListener() {
		return changeListener;
	}

	public HWItemDetailWindow setChangeListener(ChangeListener changeListener) {
		this.changeListener = changeListener;
		return this;
	}

}
