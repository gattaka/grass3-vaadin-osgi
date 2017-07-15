package cz.gattserver.grass3.hw.web;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.tokenfield.TokenField;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.util.BeanContainer;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.server.FileDownloader;
import com.vaadin.server.FileResource;
import com.vaadin.server.Resource;
import com.vaadin.server.ThemeResource;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Component;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.Label;
import com.vaadin.ui.Layout;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TabSheet.Tab;
import com.vaadin.ui.Table;
import com.vaadin.ui.UI;
import com.vaadin.ui.Upload;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.BaseTheme;

import cz.gattserver.grass3.hw.dto.HWItemDTO;
import cz.gattserver.grass3.hw.dto.HWItemOverviewDTO;
import cz.gattserver.grass3.hw.dto.HWItemTypeDTO;
import cz.gattserver.grass3.hw.dto.ServiceNoteDTO;
import cz.gattserver.grass3.hw.facade.IHWFacade;
import cz.gattserver.grass3.template.MultiUpload;
import cz.gattserver.grass3.ui.util.StringToDateConverter;
import cz.gattserver.grass3.util.MoneyFormatter;
import cz.gattserver.web.common.ui.ImageIcons;
import cz.gattserver.web.common.util.CZSuffixCreator;
import cz.gattserver.web.common.util.HumanBytesSizeCreator;
import cz.gattserver.web.common.window.ConfirmWindow;
import cz.gattserver.web.common.window.ErrorWindow;
import cz.gattserver.web.common.window.ImageDetailWindow;
import cz.gattserver.web.common.window.WebWindow;

public class HWItemDetailWindow extends WebWindow {

	private static final long serialVersionUID = -6773027334692911384L;
	private static final String DEFAULT_NOTE_LABEL_VALUE = "- Zvolte servisní záznam -";

	@Autowired
	private IHWFacade hwFacade;

	private GridLayout winLayout;
	private VerticalLayout hwImageLayout;

	private Button newNoteBtn;
	private Button fixNoteBtn;
	private Button deleteNoteBtn;

	private TabSheet sheet;
	private HWItemDTO hwItem;
	private FileDownloader downloader;
	private Long hwItemId;
	private Component triggerComponent;

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
		return new CZSuffixCreator("rok", "roky", "let").createStringWithSuffix(warrantyYears);
	}

	protected void refreshTable() {
	};

	/**
	 * Pokusí se získat ikonu HW
	 */
	private boolean tryCreateHWImage(final HWItemDTO hwItem) {

		final File icon = hwFacade.getHWItemIconFile(hwItem);
		if (icon == null)
			return false;

		String abs = icon.getAbsolutePath();
		System.out.println(abs);

		hwImageLayout.removeAllComponents();

		final Resource resource = new FileResource(icon);

		Image hwItemImage = new Image(null, resource);

		hwItemImage.setWidth("200px");

		hwImageLayout.addComponent(hwItemImage);
		hwImageLayout.setComponentAlignment(hwItemImage, Alignment.TOP_CENTER);

		HorizontalLayout btnLayout = new HorizontalLayout();
		btnLayout.setSpacing(true);

		Button hwItemImageDetailBtn = new Button("Detail",
				e -> UI.getCurrent().addWindow(new ImageDetailWindow(hwItem.getName(), icon)));

		Button hwItemImageDeleteBtn = new Button("Smazat", e -> {
			UI.getCurrent().addWindow(new ConfirmWindow("Opravdu smazat foto HW položky ?") {
				private static final long serialVersionUID = -1901927025986494370L;

				@Override
				protected void onConfirm(ClickEvent event) {
					hwFacade.deleteHWItemIconFile(hwItem);
					createHWItemImageUpload(hwItem);
				}
			});
		});

		hwItemImageDetailBtn.setIcon(new ThemeResource(ImageIcons.SEARCH_16_ICON));
		hwItemImageDeleteBtn.setIcon(new ThemeResource(ImageIcons.DELETE_16_ICON));

		btnLayout.addComponent(hwItemImageDetailBtn);
		btnLayout.addComponent(hwItemImageDeleteBtn);

		hwImageLayout.addComponent(btnLayout);
		hwImageLayout.setComponentAlignment(btnLayout, Alignment.BOTTOM_CENTER);
		return true;
	}

	/**
	 * Vytváří form pro vložení ikony HW
	 */
	private void createHWItemImageUpload(final HWItemDTO hwItem) {
		Upload upload = new Upload(null, (String filename, String mimeType) -> {
			try {
				return hwFacade.createHWItemIconOutputStream(filename, hwItem);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				return null;
			}
		});
		upload.addSucceededListener(e -> tryCreateHWImage(hwItem));
		upload.setImmediate(true);
		upload.setButtonCaption("Vložit foto");

		HorizontalLayout uploadWrapperLayout = new HorizontalLayout();
		uploadWrapperLayout.addStyleName("bordered");
		uploadWrapperLayout.setWidth("200px");
		uploadWrapperLayout.setHeight("200px");
		uploadWrapperLayout.addComponent(upload);
		uploadWrapperLayout.setComponentAlignment(upload, Alignment.MIDDLE_CENTER);

		hwImageLayout.removeAllComponents();
		hwImageLayout.addComponent(uploadWrapperLayout);
		hwImageLayout.setComponentAlignment(uploadWrapperLayout, Alignment.MIDDLE_CENTER);
	}

	private void createHWImageOrUpload(final HWItemDTO hwItem) {
		if (tryCreateHWImage(hwItem) == false) {
			createHWItemImageUpload(hwItem);
		}
	}

	private VerticalLayout createWrapperLayout(HWItemDTO hwItem) {
		VerticalLayout wrapperLayout = new VerticalLayout();
		wrapperLayout.setMargin(new MarginInfo(false, true, true, true));
		wrapperLayout.setSpacing(true);
		wrapperLayout.setSizeFull();

		Label name = new Label("<h3>" + hwItem.getName() + "</h3>", ContentMode.HTML);
		wrapperLayout.addComponent(name);

		return wrapperLayout;
	}

	private Layout createItemDetailsLayout(HWItemDTO hwItem) {

		VerticalLayout wrapperLayout = createWrapperLayout(hwItem);

		HorizontalLayout itemLayout = new HorizontalLayout();
		itemLayout.setSpacing(true);
		wrapperLayout.addComponent(itemLayout);

		/**
		 * Foto
		 */
		hwImageLayout = new VerticalLayout();
		hwImageLayout.setWidth("200px");
		hwImageLayout.setHeight("200px");
		hwImageLayout.setSpacing(true);
		itemLayout.addComponent(hwImageLayout);
		createHWImageOrUpload(hwItem);

		/**
		 * Grid
		 */
		winLayout = new GridLayout(5, 10);
		itemLayout.addComponent(winLayout);
		winLayout.setSpacing(true);

		/**
		 * Typy
		 */
		HorizontalLayout typesLayout = new HorizontalLayout();
		typesLayout.addStyleName(TokenField.STYLE_TOKENFIELD);
		typesLayout.setSpacing(true);
		for (HWItemTypeDTO type : hwItem.getTypes()) {
			Button btn = new Button(type.getName());
			btn.setStyleName(BaseTheme.BUTTON_LINK);
			typesLayout.addComponent(btn);
		}
		winLayout.addComponent(typesLayout, 1, 0, 3, 0);

		DateFormat sdf = new StringToDateConverter().getFormat();

		winLayout.addComponent(new Label("<strong>Stav</strong>", ContentMode.HTML), 1, 1);
		winLayout.getComponent(1, 1).setWidth("80px");
		winLayout.addComponent(createShiftedLabel(hwItem.getState().getName()), 1, 2);

		winLayout.addComponent(new Label("<strong>Získáno</strong>", ContentMode.HTML), 2, 1);
		winLayout.getComponent(2, 1).setWidth("80px");
		String purchDate = hwItem.getPurchaseDate() == null ? "-" : sdf.format(hwItem.getPurchaseDate());
		winLayout.addComponent(createShiftedLabel(purchDate), 2, 2);

		winLayout.addComponent(new Label("<strong>Spravováno pro</strong>", ContentMode.HTML), 3, 1);
		winLayout.getComponent(3, 1).setWidth("100px");
		winLayout.addComponent(createShiftedLabel(hwItem.getSupervizedFor() == null ? "-" : hwItem.getSupervizedFor()),
				3, 2);

		winLayout.addComponent(new Label("<strong>Cena</strong>", ContentMode.HTML), 1, 3);
		winLayout.addComponent(createShiftedLabel(createPriceString(hwItem.getPrice())), 1, 4);

		winLayout.addComponent(new Label("<strong>Odepsáno</strong>", ContentMode.HTML), 2, 3);
		String destrDate = hwItem.getDestructionDate() == null ? "-"
				: new StringToDateConverter().getFormat().format(hwItem.getDestructionDate());
		winLayout.addComponent(createShiftedLabel(destrDate), 2, 4);

		winLayout.addComponent(new Label("<strong>Záruka</strong>", ContentMode.HTML), 3, 3);
		HorizontalLayout zarukaLayout = new HorizontalLayout();
		zarukaLayout.setSpacing(true);
		String zarukaContent = createWarrantyYearsString(hwItem.getWarrantyYears());
		if (hwItem.getWarrantyYears() != null && hwItem.getWarrantyYears() > 0 && hwItem.getPurchaseDate() != null) {
			Calendar endDate = Calendar.getInstance();
			endDate.setTime(hwItem.getPurchaseDate());
			endDate.add(Calendar.YEAR, hwItem.getWarrantyYears());
			boolean isInWarranty = endDate.after(Calendar.getInstance());
			Embedded emb = new Embedded(null,
					new ThemeResource(isInWarranty ? ImageIcons.TICK_16_ICON : ImageIcons.DELETE_16_ICON));
			zarukaLayout.addComponent(emb);
			zarukaContent += " (do " + sdf.format(endDate.getTime()) + ")";
			zarukaLayout.addComponent(new Label(zarukaContent));
		} else {
			zarukaLayout.addComponent(createShiftedLabel(zarukaContent));
		}
		winLayout.addComponent(zarukaLayout, 3, 4);

		winLayout.addComponent(new Label("<strong>Je součástí</strong>", ContentMode.HTML), 1, 5);
		if (hwItem.getUsedIn() == null) {
			winLayout.addComponent(createShiftedLabel("-"), 1, 6, 4, 6);
		} else {
			Button usedInBtn = new Button(hwItem.getUsedIn().getName());
			usedInBtn.setDescription(hwItem.getUsedIn().getName());
			usedInBtn.setStyleName(BaseTheme.BUTTON_LINK);
			usedInBtn.addStyleName("shiftlabel");
			usedInBtn.addClickListener(e -> {
				close();
				UI.getCurrent().addWindow(new HWItemDetailWindow(triggerComponent, hwItem.getUsedIn().getId()));
			});
			winLayout.addComponent(usedInBtn, 1, 6, 4, 6);
		}

		List<HWItemOverviewDTO> parts = hwFacade.getAllParts(hwItem.getId());
		Label name = new Label("<h3>Součásti</h3>", ContentMode.HTML);
		wrapperLayout.addComponent(name);

		VerticalLayout partsLayout = new VerticalLayout();
		partsLayout.setSpacing(true);
		partsLayout.setMargin(true);
		Panel partsPanel = new Panel(partsLayout);
		partsPanel.setSizeFull();
		wrapperLayout.addComponent(partsPanel);
		wrapperLayout.setExpandRatio(partsPanel, 1);

		for (final HWItemOverviewDTO part : parts) {
			Button partDetailBtn = new Button(part.getName());
			partDetailBtn.setDescription(part.getName());
			partDetailBtn.setStyleName(BaseTheme.BUTTON_LINK);
			partDetailBtn.addStyleName("shiftlabel");
			partDetailBtn.addClickListener(e -> {
				close();
				HWItemDTO detailTO = hwFacade.getHWItem(part.getId());
				UI.getCurrent().addWindow(new HWItemDetailWindow(triggerComponent, detailTO.getId()));
			});
			partsLayout.addComponent(partDetailBtn);
		}

		HorizontalLayout operationsLayout = new HorizontalLayout();
		operationsLayout.setSpacing(true);
		wrapperLayout.addComponent(operationsLayout);

		final Button fixBtn = new Button("Upravit", new ThemeResource(ImageIcons.QUICKEDIT_16_ICON));
		final Button deleteBtn = new Button("Smazat", new ThemeResource(ImageIcons.DELETE_16_ICON));

		/**
		 * Oprava údajů existující položky HW
		 */
		fixBtn.addClickListener(e -> {
			UI.getCurrent().addWindow(new HWItemCreateWindow(HWItemDetailWindow.this, hwItem.getId()) {

				private static final long serialVersionUID = -1397391593801030584L;

				@Override
				protected void onSuccess() {
					refreshTable();
					createFirstTab();
					sheet.setSelectedTab(0);
				}
			});
		});
		operationsLayout.addComponent(fixBtn);

		/**
		 * Smazání položky HW
		 */
		deleteBtn.addClickListener(e -> {
			deleteBtn.setEnabled(false);
			UI.getCurrent().addWindow(new ConfirmWindow("Opravdu smazat '" + hwItem.getName()
					+ "' (budou smazány i servisní záznamy a údaje u součástí) ?") {

				private static final long serialVersionUID = -422763987707688597L;

				@Override
				protected void onConfirm(ClickEvent event) {
					if (hwFacade.deleteHWItem(hwItem)) {
						refreshTable();
						HWItemDetailWindow.this.close();
					} else {
						UI.getCurrent().addWindow(new ErrorWindow("Nezdařilo se smazat vybranou položku"));
					}
				}

				@Override
				public void close() {
					deleteBtn.setEnabled(true);
					super.close();
				}

			});
		});
		operationsLayout.addComponent(deleteBtn);

		return wrapperLayout;
	}

	private void sortServiceNotes(Table table) {
		table.sort(new Object[] { "date", "id" }, new boolean[] { false, false });
	}

	private Layout createServiceNotesTab() {
		VerticalLayout wrapperLayout = createWrapperLayout(hwItem);

		/**
		 * Tabulka záznamů
		 */
		final Table table = new Table();
		table.setSelectable(true);
		table.setImmediate(true);

		final BeanContainer<Long, ServiceNoteDTO> notesContainer = new BeanContainer<Long, ServiceNoteDTO>(
				ServiceNoteDTO.class);
		notesContainer.setBeanIdProperty("id");
		notesContainer.addAll(hwItem.getServiceNotes());

		table.setContainerDataSource(notesContainer);
		table.setConverter("date", new StringToDateConverter());

		table.setColumnHeader("date", "Datum");
		table.setColumnHeader("state", "Stav");
		table.setColumnHeader("usedInName", "Je součástí");
		table.setColumnHeader("description", "Obsah");
		table.setColumnWidth("description", 200);
		table.setVisibleColumns(new Object[] { "date", "state", "usedInName", "description" });
		table.setWidth("100%");
		table.setHeight("200px");

		sortServiceNotes(table);

		wrapperLayout.addComponent(table);

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

		table.addValueChangeListener(new ValueChangeListener() {

			private static final long serialVersionUID = -8943196289027284739L;

			@Override
			public void valueChange(ValueChangeEvent event) {
				if (table.getValue() != null) {
					BeanContainer<?, ?> cont = (BeanContainer<?, ?>) table.getContainerDataSource();
					BeanItem<?> item = cont.getItem(table.getValue());
					ServiceNoteDTO serviceNoteDTO = (ServiceNoteDTO) item.getBean();
					serviceNoteDescription.setValue((String) serviceNoteDTO.getDescription());
				} else {
					serviceNoteDescription.setValue(DEFAULT_NOTE_LABEL_VALUE);
				}
				fixNoteBtn.setEnabled(table.getValue() != null);
				deleteNoteBtn.setEnabled(table.getValue() != null);
			}
		});

		HorizontalLayout operationsLayout = new HorizontalLayout();
		operationsLayout.setSpacing(true);
		wrapperLayout.addComponent(operationsLayout);

		/**
		 * Založení nového servisního záznamu
		 */
		newNoteBtn = new Button("Přidat záznam");
		newNoteBtn.setImmediate(true);
		newNoteBtn.setIcon(new ThemeResource(ImageIcons.PENCIL_16_ICON));
		newNoteBtn.addClickListener(new Button.ClickListener() {
			private static final long serialVersionUID = 8876001665427003203L;

			public void buttonClick(ClickEvent event) {
				UI.getCurrent().addWindow(new ServiceNoteCreateWindow(newNoteBtn, hwItem) {

					private static final long serialVersionUID = -5582822648042555576L;

					@Override
					protected void onSuccess(ServiceNoteDTO noteDTO) {
						refreshTable();
						createFirstTab();
						notesContainer.addItem(noteDTO.getId(), noteDTO);
						sortServiceNotes(table);
						table.select(noteDTO.getId());
						table.setCurrentPageFirstItemId(noteDTO.getId());
					}
				});
			}
		});
		operationsLayout.addComponent(newNoteBtn);

		/**
		 * Úprava záznamu
		 */
		fixNoteBtn = new Button("Opravit záznam");
		fixNoteBtn.setEnabled(false);
		fixNoteBtn.setImmediate(true);
		fixNoteBtn.setIcon(new ThemeResource(ImageIcons.QUICKEDIT_16_ICON));
		fixNoteBtn.addClickListener(new Button.ClickListener() {
			private static final long serialVersionUID = 8876001665427003203L;

			public void buttonClick(ClickEvent event) {
				Object id = table.getValue();
				@SuppressWarnings("unchecked")
				BeanItem<ServiceNoteDTO> item = (BeanItem<ServiceNoteDTO>) table.getItem(id);

				UI.getCurrent().addWindow(new ServiceNoteCreateWindow(newNoteBtn, hwItem, item.getBean()) {

					private static final long serialVersionUID = -5582822648042555576L;

					@Override
					protected void onSuccess(ServiceNoteDTO noteDTO) {
						table.removeItem(noteDTO.getId());
						notesContainer.addItem(noteDTO.getId(), noteDTO);
						sortServiceNotes(table);
						table.select(noteDTO.getId());
						table.setCurrentPageFirstItemId(noteDTO.getId());
					}
				});
			}
		});
		operationsLayout.addComponent(fixNoteBtn);

		/**
		 * Smazání záznamu
		 */
		deleteNoteBtn = new Button("Smazat záznam");
		deleteNoteBtn.setEnabled(false);
		deleteNoteBtn.setIcon(new ThemeResource(ImageIcons.DELETE_16_ICON));
		deleteNoteBtn.addClickListener(new Button.ClickListener() {

			private static final long serialVersionUID = 4983897852548880141L;

			@Override
			public void buttonClick(ClickEvent event) {
				Object id = table.getValue();
				@SuppressWarnings("unchecked")
				BeanItem<ServiceNoteDTO> item = (BeanItem<ServiceNoteDTO>) table.getItem(id);
				UI.getCurrent().addWindow(new ConfirmWindow("Opravdu smazat vybraný servisní záznam?") {

					private static final long serialVersionUID = -422763987707688597L;

					@Override
					protected void onConfirm(ClickEvent event) {
						hwFacade.deleteServiceNote(item.getBean(), hwItem);
						table.removeItem(item.getBean().getId());
					}

					@Override
					public void close() {
						deleteNoteBtn.setEnabled(true);
						super.close();
					}
				});
			}
		});
		operationsLayout.addComponent(deleteNoteBtn);

		return wrapperLayout;
	}

	private Layout createPhotosTab() {
		VerticalLayout wrapperLayout = createWrapperLayout(hwItem);

		GridLayout listLayout = new GridLayout();
		listLayout.setColumns(4);
		listLayout.setSpacing(true);
		listLayout.setMargin(true);

		Panel panel = new Panel(listLayout);
		panel.setSizeFull();
		wrapperLayout.addComponent(panel);
		wrapperLayout.setExpandRatio(panel, 1);

		createImagesList(listLayout);

		HorizontalLayout uploadWrapperLayout = new HorizontalLayout();
		uploadWrapperLayout.setWidth("100%");
		uploadWrapperLayout.setMargin(true);
		wrapperLayout.addComponent(uploadWrapperLayout);

		MultiUpload multiFileUpload = new MultiUpload() {
			private static final long serialVersionUID = -3899558855555370125L;

			@Override
			protected void handleFile(InputStream in, String fileName, String mimeType, long length) {
				hwFacade.saveImagesFile(in, fileName, hwItem);

				// refresh listu
				listLayout.removeAllComponents();
				createImagesList(listLayout);
			}
		};

		multiFileUpload.setCaption("Vložit fotografie");
		multiFileUpload.setSizeUndefined();
		uploadWrapperLayout.addStyleName("bordered");
		uploadWrapperLayout.addComponent(multiFileUpload);
		uploadWrapperLayout.setComponentAlignment(multiFileUpload, Alignment.MIDDLE_CENTER);

		return wrapperLayout;
	}

	private void createImagesList(GridLayout listLayout) {

		for (final File file : hwFacade.getHWItemImagesFiles(hwItem)) {

			VerticalLayout imageLayout = new VerticalLayout();
			listLayout.addComponent(imageLayout);
			imageLayout.setSpacing(true);

			Resource resource = new FileResource(file);
			Image img = new Image(null, resource);
			img.setWidth("200px");
			imageLayout.addComponent(img);

			HorizontalLayout btnLayout = new HorizontalLayout();
			btnLayout.setSpacing(true);

			Button hwItemImageDetailBtn = new Button("Detail",
					e -> UI.getCurrent().addWindow(new ImageDetailWindow(hwItem.getName(), file)));

			Button hwItemImageDeleteBtn = new Button("Smazat",
					e -> UI.getCurrent().addWindow(new ConfirmWindow("Opravdu smazat foto HW položky ?") {
						private static final long serialVersionUID = -1901927025986494370L;

						@Override
						protected void onConfirm(ClickEvent event) {
							hwFacade.deleteHWItemFile(hwItem, file);

							// refresh listu
							listLayout.removeAllComponents();
							createImagesList(listLayout);
						}
					}));

			hwItemImageDetailBtn.setIcon(new ThemeResource(ImageIcons.SEARCH_16_ICON));
			hwItemImageDeleteBtn.setIcon(new ThemeResource(ImageIcons.DELETE_16_ICON));

			btnLayout.addComponent(hwItemImageDetailBtn);
			btnLayout.addComponent(hwItemImageDeleteBtn);

			imageLayout.addComponent(btnLayout);
			imageLayout.setComponentAlignment(btnLayout, Alignment.BOTTOM_CENTER);
		}
	}

	private Layout createDocsTab() {
		VerticalLayout wrapperLayout = createWrapperLayout(hwItem);

		final Table table = new Table();
		table.setSizeFull();
		wrapperLayout.addComponent(table);
		wrapperLayout.setExpandRatio(table, 1);

		createDocumentsList(table);

		HorizontalLayout uploadWrapperLayout = new HorizontalLayout();
		uploadWrapperLayout.setWidth("100%");
		uploadWrapperLayout.setMargin(true);
		wrapperLayout.addComponent(uploadWrapperLayout);

		final MultiUpload multiFileUpload = new MultiUpload() {
			private static final long serialVersionUID = 8500364606014524121L;

			@Override
			public void handleFile(InputStream in, String fileName, String mime, long size) {

				hwFacade.saveDocumentsFile(in, fileName, hwItem);

				// refresh listu
				table.removeAllItems();
				createDocumentsList(table);
			}

		};
		multiFileUpload.setCaption("Vložit dokumenty");
		multiFileUpload.setSizeUndefined();
		uploadWrapperLayout.addStyleName("bordered");
		uploadWrapperLayout.addComponent(multiFileUpload);

		final Button hwItemDocumentDownloadBtn = new Button("Stáhnout", new ThemeResource(ImageIcons.DOWN_16_ICON));
		uploadWrapperLayout.addComponent(hwItemDocumentDownloadBtn);
		hwItemDocumentDownloadBtn.setEnabled(false);

		final Button hwItemDocumentDeleteBtn = new Button("Smazat", e -> {
			File file = (File) table.getValue();
			if (file == null)
				return;

			UI.getCurrent().addWindow(new ConfirmWindow("Opravdu smazat '" + file.getName() + "' ?") {
				private static final long serialVersionUID = -1901927025986494370L;

				@Override
				protected void onConfirm(ClickEvent event) {
					hwFacade.deleteHWItemFile(hwItem, file);

					// refresh listu
					table.removeAllItems();
					createDocumentsList(table);
				}
			});
		});
		hwItemDocumentDeleteBtn.setEnabled(false);
		hwItemDocumentDeleteBtn.setIcon(new ThemeResource(ImageIcons.DELETE_16_ICON));
		uploadWrapperLayout.addComponent(hwItemDocumentDeleteBtn);

		table.addValueChangeListener(new ValueChangeListener() {

			private static final long serialVersionUID = -8943196289027284739L;

			@Override
			public void valueChange(ValueChangeEvent event) {
				if (downloader != null) {
					downloader.remove();
					downloader = null;
				}
				if (table.getValue() != null) {
					File file = (File) table.getValue();
					hwItemDocumentDeleteBtn.setEnabled(true);
					hwItemDocumentDownloadBtn.setEnabled(true);
					downloader = new FileDownloader(new FileResource(file));
					downloader.extend(hwItemDocumentDownloadBtn);
				} else {
					hwItemDocumentDownloadBtn.setEnabled(false);
					hwItemDocumentDeleteBtn.setEnabled(false);
				}
			}
		});

		return wrapperLayout;
	}

	private void createDocumentsList(Table table) {

		final BeanItemContainer<File> cont = new BeanItemContainer<File>(File.class);
		cont.addAll(Arrays.asList(hwFacade.getHWItemDocumentsFiles(hwItem)));
		table.setContainerDataSource(cont);

		table.addGeneratedColumn("fileSize", (Table source, Object itemId, Object columnId) -> {
			File file = (File) itemId;
			Label sizelabel = new Label(HumanBytesSizeCreator.format(file.length(), true));
			sizelabel.setDescription(file.length() + "B");
			sizelabel.setSizeUndefined();
			return sizelabel;
		});

		table.addGeneratedColumn("datum", (Table source, Object itemId, Object columnId) -> {
			File file = (File) itemId;
			SimpleDateFormat sdf = new SimpleDateFormat("d.MM.yyyy");
			Label label = new Label(sdf.format(new Date(file.lastModified())));
			return label;
		});

		table.setVisibleColumns("name", "datum", "fileSize");
		table.setColumnHeader("name", "Název");
		table.setColumnHeader("datum", "Datum");
		table.setColumnHeader("fileSize", "Velikost");
	}

	public HWItemDetailWindow(Component triggerComponent, final Long hwItemId) {
		super("Detail HW");
		this.hwItemId = hwItemId;
		this.triggerComponent = triggerComponent;

		setWidth("900px");
		setHeight("700px");

		sheet = new TabSheet();
		sheet.setSizeFull();
		createFirstTab();
		sheet.addTab(createServiceNotesTab(), "Záznamy", new ThemeResource(ImageIcons.CLIPBOARD_16_ICON));
		sheet.addTab(createPhotosTab(), "Fotografie", new ThemeResource(ImageIcons.IMG_16_ICON));
		sheet.addTab(createDocsTab(), "Dokumentace", new ThemeResource(ImageIcons.DOCUMENT_16_ICON));
		setContent(sheet);

		triggerComponent.setEnabled(false);

		addCloseListener(e -> {
			if (triggerComponent != null)
				triggerComponent.setEnabled(true);
		});

		center();
	}

	private void createFirstTab() {
		this.hwItem = hwFacade.getHWItem(hwItemId);
		Tab tab = sheet.getTab(0);
		if (tab != null)
			sheet.removeTab(tab);
		sheet.addTab(createItemDetailsLayout(hwItem), "Info", new ThemeResource(ImageIcons.GEAR2_16_ICON), 0);
	}
}
