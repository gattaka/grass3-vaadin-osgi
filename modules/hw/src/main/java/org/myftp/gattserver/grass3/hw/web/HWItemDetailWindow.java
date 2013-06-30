package org.myftp.gattserver.grass3.hw.web;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.OutputStream;
import java.util.List;
import java.util.Set;

import org.myftp.gattserver.grass3.SpringContextHelper;
import org.myftp.gattserver.grass3.hw.dto.HWItemDTO;
import org.myftp.gattserver.grass3.hw.dto.HWItemFileDTO;
import org.myftp.gattserver.grass3.hw.dto.HWItemTypeDTO;
import org.myftp.gattserver.grass3.hw.dto.ServiceNoteDTO;
import org.myftp.gattserver.grass3.hw.facade.IHWFacade;
import org.myftp.gattserver.grass3.subwindows.ConfirmSubwindow;
import org.myftp.gattserver.grass3.subwindows.GrassSubWindow;
import org.myftp.gattserver.grass3.subwindows.ImageDetailSubwindow;
import org.myftp.gattserver.grass3.ui.util.GrassStringToDateConverter;
import org.myftp.gattserver.grass3.ui.util.GrassStringToMoneyConverter;
import org.myftp.gattserver.grass3.util.CZSuffixCreator;
import org.myftp.gattserver.grass3.util.StringPreviewCreator;
import org.vaadin.tokenfield.TokenField;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.util.BeanContainer;
import com.vaadin.data.util.BeanItem;
import com.vaadin.server.ExternalResource;
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
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Table;
import com.vaadin.ui.UI;
import com.vaadin.ui.Upload;
import com.vaadin.ui.Upload.SucceededEvent;
import com.vaadin.ui.Upload.SucceededListener;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.BaseTheme;

public class HWItemDetailWindow extends GrassSubWindow {

	private static final long serialVersionUID = -6773027334692911384L;
	private static final String DEFAULT_NOTE_LABEL_VALUE = "- Zvolte servisní záznam -";

	private IHWFacade hwFacade;

	private GridLayout winLayout;
	private VerticalLayout hwImageLayout;

	private Label createShiftedLabel(String caption) {
		Label label = new Label(caption, ContentMode.HTML);
		label.addStyleName("shiftlabel");
		return label;
	}

	private String createPriceString(Integer price) {
		if (price == null)
			return "-";
		return GrassStringToMoneyConverter.format(price);
	}

	private String createWarrantyYearsString(Integer warrantyYears) {
		return new CZSuffixCreator("rok", "roky", "let")
				.createStringWithSuffix(warrantyYears);
	}

	private void createFilesList(final GridLayout filesLayout,
			final HWItemDTO hwItem, final boolean document) {

		VerticalLayout targetLayout = new VerticalLayout();
		targetLayout.addComponent(new Label("<strong>"
				+ (document ? "Dokumenty" : "Fotografie") + "</strong>",
				ContentMode.HTML));

		Set<HWItemFileDTO> files = document ? hwItem.getDocuments() : hwItem
				.getImages();

		if (files.isEmpty()) {
			targetLayout.addComponent(createShiftedLabel("-"));
		} else {
			for (HWItemFileDTO fileDTO : files) {
				Button docDetailBtn = new Button(fileDTO.getDescription());
				docDetailBtn.setStyleName(BaseTheme.BUTTON_LINK);
				docDetailBtn.addStyleName("shiftlabel");
				FileDownloader downloader = new FileDownloader(
						new ExternalResource(fileDTO.getLink()));
				downloader.extend(docDetailBtn);
				targetLayout.addComponent(docDetailBtn);
			}
		}

		Label separator = new Label("");
		separator.setHeight("10px");
		targetLayout.addComponent(separator);

		Button newDocBtn = new Button("Přidat");
		newDocBtn.setIcon(new ThemeResource("img/tags/plus_16.png"));
		newDocBtn.addClickListener(new Button.ClickListener() {

			private static final long serialVersionUID = 4983897852548880141L;

			@Override
			public void buttonClick(ClickEvent event) {
				UI.getCurrent().addWindow(
						new HWItemFileCreateWindow(hwItem, document) {

							private static final long serialVersionUID = -7010801549731161045L;

							@Override
							protected void onSuccess() {
								HWItemDTO freshItem = hwFacade.getHWItem(hwItem
										.getId());
								createFilesList(filesLayout, freshItem,
										document);
							}
						});
			}
		});
		targetLayout.addComponent(newDocBtn);

		filesLayout.removeComponent(document ? 0 : 1, 0);
		filesLayout.addComponent(targetLayout, document ? 0 : 1, 0);
	}

	/**
	 * Pokusí se získat ikonu HW
	 */
	private boolean tryCreateHWImage(final HWItemDTO hwItem) {

		final File icon = hwFacade.getHWItemImageFile(hwItem);
		if (icon == null)
			return false;
		
		String abs = icon.getAbsolutePath();
		System.out.println(abs);

		hwImageLayout.removeAllComponents();

		final Resource resource = new FileResource(icon);

		Image hwItemImage = new Image(null, resource);

		hwItemImage.setWidth("200px");
		// hwItemImage.setHeight("200px");

		hwImageLayout.addComponent(hwItemImage);
		hwImageLayout.setComponentAlignment(hwItemImage, Alignment.TOP_CENTER);

		HorizontalLayout btnLayout = new HorizontalLayout();
		btnLayout.setSpacing(true);

		Button hwItemImageDetailBtn = new Button("Detail",
				new Button.ClickListener() {
					private static final long serialVersionUID = 3574387596782957413L;

					@Override
					public void buttonClick(ClickEvent event) {
						UI.getCurrent()
								.addWindow(
										new ImageDetailSubwindow(hwItem
												.getName(), icon));
					}
				});

		Button hwItemImageDeleteBtn = new Button("Smazat",
				new Button.ClickListener() {
					private static final long serialVersionUID = 3574387596782957413L;

					@Override
					public void buttonClick(ClickEvent event) {
						UI.getCurrent().addWindow(
								new ConfirmSubwindow(
										"Opravdu smazat foto HW položky ?") {
									private static final long serialVersionUID = -1901927025986494370L;

									@Override
									protected void onConfirm(ClickEvent event) {
										hwFacade.deleteHWItemImageFile(hwItem);
										createImageUpload(hwItem);
									}
								});
					}
				});

		hwItemImageDetailBtn
				.setIcon(new ThemeResource("img/tags/search_16.png"));
		hwItemImageDeleteBtn
				.setIcon(new ThemeResource("img/tags/delete_16.png"));

		btnLayout.addComponent(hwItemImageDetailBtn);
		btnLayout.addComponent(hwItemImageDeleteBtn);

		hwImageLayout.addComponent(btnLayout);
		hwImageLayout.setComponentAlignment(btnLayout, Alignment.BOTTOM_CENTER);
		return true;
	}

	/**
	 * Vytváří form pro vložení ikony HW
	 */
	private void createImageUpload(final HWItemDTO hwItem) {
		Upload upload = new Upload(null, new Upload.Receiver() {
			private static final long serialVersionUID = 3405904713188904034L;

			@Override
			public OutputStream receiveUpload(String filename, String mimeType) {
				try {
					return hwFacade.createHWItemImageOutputStream(filename,
							hwItem);
				} catch (FileNotFoundException e) {
					e.printStackTrace();
					return null;
				}
			}
		});
		upload.addSucceededListener(new SucceededListener() {
			private static final long serialVersionUID = 8510503781150063264L;

			@Override
			public void uploadSucceeded(SucceededEvent event) {
				tryCreateHWImage(hwItem);
			}
		});
		upload.setImmediate(true);
		upload.setButtonCaption("Vložit foto");

		HorizontalLayout uploadWrapperLayout = new HorizontalLayout();
		uploadWrapperLayout.addStyleName("bordered");
		uploadWrapperLayout.setWidth("200px");
		uploadWrapperLayout.setHeight("200px");
		uploadWrapperLayout.addComponent(upload);
		uploadWrapperLayout.setComponentAlignment(upload,
				Alignment.MIDDLE_CENTER);

		hwImageLayout.removeAllComponents();
		hwImageLayout.addComponent(uploadWrapperLayout);
		hwImageLayout.setComponentAlignment(uploadWrapperLayout,
				Alignment.MIDDLE_CENTER);
	}

	private void createHWImageOrUpload(final HWItemDTO hwItem) {
		if (tryCreateHWImage(hwItem) == false) {
			createImageUpload(hwItem);
		}
	}

	public HWItemDetailWindow(final Component triggerComponent,
			final HWItemDTO hwItem) {
		super(hwItem.getName());

		hwFacade = SpringContextHelper.getBean(IHWFacade.class);

		setWidth("850px");
		setHeight("780px");

		triggerComponent.setEnabled(false);

		VerticalLayout layout = new VerticalLayout();
		setContent(layout);
		layout.setSpacing(true);
		layout.setMargin(true);

		winLayout = new GridLayout(4, 10);
		layout.addComponent(winLayout);
		layout.setExpandRatio(winLayout, 1);
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

		/**
		 * Foto
		 */
		hwImageLayout = new VerticalLayout();
		hwImageLayout.setWidth("200px");
		hwImageLayout.setSpacing(true);
		winLayout.addComponent(hwImageLayout, 0, 0, 0, 8);
		winLayout.setComponentAlignment(hwImageLayout, Alignment.TOP_LEFT);
		createHWImageOrUpload(hwItem);

		// roztahovač
		Label resizer = new Label();
		resizer.setWidth("210px");
		winLayout.addComponent(resizer, 0, 9);

		/**
		 * Info pole - první sloupec
		 */
		winLayout.addComponent(new Label("<strong>Stav</strong>",
				ContentMode.HTML), 1, 1);
		winLayout.getComponent(1, 1).setWidth("70px");
		winLayout.addComponent(createShiftedLabel(hwItem.getState().getName()),
				1, 2);

		winLayout.addComponent(new Label("<strong>Cena</strong>",
				ContentMode.HTML), 1, 3);
		winLayout.addComponent(
				createShiftedLabel(createPriceString(hwItem.getPrice())), 1, 4);

		winLayout.addComponent(new Label("<strong>Záruka</strong>",
				ContentMode.HTML), 1, 5);
		winLayout.addComponent(
				createShiftedLabel(createWarrantyYearsString(hwItem
						.getWarrantyYears())), 1, 6);

		/**
		 * Info pole - druhý sloupec
		 */
		winLayout.addComponent(new Label("<strong>Získáno</strong>",
				ContentMode.HTML), 2, 1);
		winLayout.getComponent(2, 1).setWidth("80px");
		String purchDate = hwItem.getPurchaseDate() == null ? "-"
				: GrassStringToDateConverter.format(hwItem.getPurchaseDate());
		winLayout.addComponent(createShiftedLabel(purchDate), 2, 2);

		winLayout.addComponent(new Label("<strong>Odepsáno</strong>",
				ContentMode.HTML), 2, 3);
		String destrDate = hwItem.getDestructionDate() == null ? "-"
				: GrassStringToDateConverter
						.format(hwItem.getDestructionDate());
		winLayout.addComponent(createShiftedLabel(destrDate), 2, 4);

		/**
		 * Součásti
		 */
		winLayout.addComponent(new Label("<strong>Je součástí</strong>",
				ContentMode.HTML), 3, 1);
		winLayout.getComponent(3, 1).setWidth("100px");
		if (hwItem.getUsedIn() == null) {
			winLayout.addComponent(createShiftedLabel("-"), 3, 2);
		} else {
			Button usedInBtn = new Button(StringPreviewCreator.createPreview(
					hwItem.getUsedIn().getName(), 60));
			usedInBtn.setDescription(hwItem.getUsedIn().getName());
			usedInBtn.setStyleName(BaseTheme.BUTTON_LINK);
			usedInBtn.addStyleName("shiftlabel");
			usedInBtn.addClickListener(new Button.ClickListener() {

				private static final long serialVersionUID = 4983897852548880141L;

				@Override
				public void buttonClick(ClickEvent event) {
					close();
					UI.getCurrent().addWindow(
							new HWItemDetailWindow(triggerComponent, hwItem
									.getUsedIn()));
				}
			});
			winLayout.addComponent(usedInBtn, 3, 2);
		}

		winLayout.addComponent(new Label("<strong>Součásti</strong>",
				ContentMode.HTML), 3, 3);
		VerticalLayout partsLayout = new VerticalLayout();
		winLayout.addComponent(partsLayout, 3, 4, 3, 8);
		List<HWItemDTO> parts = hwFacade.getAllParts(hwItem.getId());
		if (parts.isEmpty())
			partsLayout.addComponent(createShiftedLabel("-"));
		for (final HWItemDTO part : parts) {
			Button partDetailBtn = new Button(
					StringPreviewCreator.createPreview(part.getName(), 60));
			partDetailBtn.setDescription(part.getName());
			partDetailBtn.setStyleName(BaseTheme.BUTTON_LINK);
			partDetailBtn.addStyleName("shiftlabel");
			partDetailBtn.addClickListener(new Button.ClickListener() {

				private static final long serialVersionUID = 4983897852548880141L;

				@Override
				public void buttonClick(ClickEvent event) {
					close();
					UI.getCurrent().addWindow(
							new HWItemDetailWindow(triggerComponent, part));
				}
			});
			partsLayout.addComponent(partDetailBtn);
		}

		/**
		 * Střední část - dokumenty
		 */
		GridLayout filesLayout = new GridLayout(2, 1);
		layout.addComponent(filesLayout);
		filesLayout.setWidth("100%");
		filesLayout.setSpacing(true);

		// dokumenty
		createFilesList(filesLayout, hwItem, true);

		// fotografie
		createFilesList(filesLayout, hwItem, false);

		/**
		 * Spodní část
		 */
		HorizontalLayout horizontalLayout = new HorizontalLayout();
		layout.addComponent(horizontalLayout);
		horizontalLayout.setWidth("100%");
		horizontalLayout.setSpacing(true);
		horizontalLayout.setMargin(new MarginInfo(true, false, false, false));

		/**
		 * Tabulka záznamů
		 */
		final Table table = new Table();
		table.setSelectable(true);
		table.setImmediate(true);

		BeanContainer<Long, ServiceNoteDTO> filesContainer = new BeanContainer<Long, ServiceNoteDTO>(
				ServiceNoteDTO.class);
		filesContainer.setBeanIdProperty("id");
		filesContainer.addAll(hwItem.getServiceNotes());

		table.setContainerDataSource(filesContainer);
		table.setConverter("date", GrassStringToDateConverter.getInstance());
		table.setConverter("state", new StringToHWItemStateConverter());
		table.setConverter("usedIn", new StringToHWItemConverter());

		table.setColumnHeader("date", "Datum");
		table.setColumnHeader("state", "Stav");
		table.setColumnHeader("usedIn", "Je součástí");
		table.setVisibleColumns(new String[] { "date", "state", "usedIn" });
		table.setWidth("450px");
		table.setHeight("200px");

		table.setSortContainerPropertyId("date");
		table.setSortAscending(false);

		horizontalLayout.addComponent(table);

		/**
		 * Detail záznamu
		 */
		final Label serviceNoteDescription = new Label(DEFAULT_NOTE_LABEL_VALUE);
		Panel panel = new Panel(serviceNoteDescription);
		panel.setStyleName("hw-panel");
		panel.setHeight("200px");
		panel.setWidth("100%");
		horizontalLayout.addComponent(panel);
		horizontalLayout.setExpandRatio(panel, 1);

		table.addValueChangeListener(new ValueChangeListener() {

			private static final long serialVersionUID = -8943196289027284739L;

			@Override
			public void valueChange(ValueChangeEvent event) {
				if (table.getValue() != null) {
					BeanContainer<?, ?> cont = (BeanContainer<?, ?>) table
							.getContainerDataSource();
					BeanItem<?> item = cont.getItem(table.getValue());
					ServiceNoteDTO serviceNoteDTO = (ServiceNoteDTO) item
							.getBean();
					serviceNoteDescription.setValue((String) serviceNoteDTO
							.getDescription());
				} else {
					serviceNoteDescription.setValue(DEFAULT_NOTE_LABEL_VALUE);
				}

			}
		});

		addCloseListener(new CloseListener() {

			private static final long serialVersionUID = 1435044338717794371L;

			@Override
			public void windowClose(CloseEvent e) {
				if (triggerComponent != null)
					triggerComponent.setEnabled(true);
			}

		});

		center();

	}
}
