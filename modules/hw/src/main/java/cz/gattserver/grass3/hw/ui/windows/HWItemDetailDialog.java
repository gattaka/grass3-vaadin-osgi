package cz.gattserver.grass3.hw.ui.windows;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
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
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.Grid.Column;
import com.vaadin.flow.component.grid.Grid.SelectionMode;
import com.vaadin.flow.component.grid.GridSortOrder;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.data.provider.SortDirection;
import com.vaadin.flow.server.StreamResource;

import cz.gattserver.common.util.CZAmountFormatter;
import cz.gattserver.common.util.MoneyFormatter;
import cz.gattserver.grass3.exception.GrassException;
import cz.gattserver.grass3.hw.interfaces.HWItemTO;
import cz.gattserver.grass3.hw.HWConfiguration;
import cz.gattserver.grass3.hw.interfaces.HWItemFileTO;
import cz.gattserver.grass3.hw.interfaces.HWItemOverviewTO;
import cz.gattserver.grass3.hw.interfaces.ServiceNoteTO;
import cz.gattserver.grass3.hw.service.HWService;
import cz.gattserver.grass3.ui.components.button.CreateButton;
import cz.gattserver.grass3.ui.components.button.DeleteButton;
import cz.gattserver.grass3.ui.components.button.DeleteGridButton;
import cz.gattserver.grass3.ui.components.button.ModifyButton;
import cz.gattserver.grass3.ui.components.button.ModifyGridButton;
import cz.gattserver.grass3.ui.pages.template.GrassPage;
import cz.gattserver.grass3.ui.util.GridUtils;
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
				createItemDetailsLayout();
				break;
			case 1:
				switchImgTab();
				break;
			}
		});
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

	private void createItemDetailsLayout() {
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
				createItemDetailsLayout();
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
		Button newNoteBtn = new CreateButton(e -> UI.getCurrent().addWindow(new ServiceNoteCreateDialog(hwItem) {
			private static final long serialVersionUID = -5582822648042555576L;

			@Override
			protected void onSuccess(ServiceNoteTO noteDTO) {
				if (changeListener != null)
					changeListener.onChange();
				createFirstTab();
				populateServiceNotesGrid();
				serviceNotesGrid.select(noteDTO);
				tabs.getTab(1).setCaption(createServiceNotesTabCaption());
			}
		}));

		/**
		 * Úprava záznamu
		 */
		Button fixNoteBtn = new ModifyGridButton<>("Opravit záznam", event -> {
			if (serviceNotesGrid.getSelectedItems().isEmpty())
				return;
			UI.getCurrent().addWindow(
					new ServiceNoteCreateDialog(hwItem, serviceNotesGrid.getSelectedItems().iterator().next()) {
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
			tabs.getTab(1).setCaption(createServiceNotesTabCaption());
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
		listLayout.setPadding(true);

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
				tabs.getTab(2).setCaption(createPhotosTabCaption());
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
			imageLayout.setPadding(false);

			Resource resource = new StreamResource(
					() -> getHWService().getHWItemImagesFileInputStream(hwItemId, file.getName()), file.getName());
			Embedded img = new Embedded(null, resource);
			img.addStyleName("thumbnail-200");
			imageLayout.addComponent(img);

			HorizontalLayout btnLayout = new HorizontalLayout();
			btnLayout.setSpacing(true);

			Button hwItemImageDetailBtn = new Button("Detail", e -> Page.getCurrent()
					.open(HWConfiguration.HW_PATH + "/" + hwItem.getId() + "/img/" + file.getName(), file.getName()));

			Button hwItemImageDeleteBtn = new DeleteButton(
					e -> UI.getCurrent().addWindow(new ConfirmDialog("Opravdu smazat foto HW položky ?", ev -> {
						getHWService().deleteHWItemImagesFile(hwItemId, file.getName());

						// refresh listu
						listLayout.removeAllComponents();
						createImagesList(listLayout);
						tabs.getTab(2).setCaption(createPhotosTabCaption());
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
				tabs.getTab(3).setCaption(createDocsTabCaption());
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
			tabs.getTab(3).setCaption(createDocsTabCaption());
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
