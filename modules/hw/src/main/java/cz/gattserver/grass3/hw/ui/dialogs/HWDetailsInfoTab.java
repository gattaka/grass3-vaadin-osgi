package cz.gattserver.grass3.hw.ui.dialogs;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.server.StreamResource;

import cz.gattserver.common.util.CZAmountFormatter;
import cz.gattserver.common.util.MoneyFormatter;
import cz.gattserver.grass3.hw.HWConfiguration;
import cz.gattserver.grass3.hw.interfaces.HWItemOverviewTO;
import cz.gattserver.grass3.hw.interfaces.HWItemTO;
import cz.gattserver.grass3.hw.service.HWService;
import cz.gattserver.grass3.ui.components.button.CloseButton;
import cz.gattserver.grass3.ui.components.button.DeleteButton;
import cz.gattserver.grass3.ui.components.button.ModifyButton;
import cz.gattserver.grass3.ui.util.ButtonLayout;
import cz.gattserver.grass3.ui.util.ContainerDiv;
import cz.gattserver.grass3.ui.util.GridLayout;
import cz.gattserver.grass3.ui.util.UIUtils;
import cz.gattserver.web.common.spring.SpringContextHelper;
import cz.gattserver.web.common.ui.ImageIcon;
import cz.gattserver.web.common.ui.Strong;
import cz.gattserver.web.common.ui.window.ConfirmDialog;
import cz.gattserver.web.common.ui.window.ErrorDialog;

public class HWDetailsInfoTab extends Div {

	private static final long serialVersionUID = 8602793883158440889L;

	private static final Logger logger = LoggerFactory.getLogger(HWDetailsInfoTab.class);

	@Autowired
	private HWService hwService;

	private VerticalLayout hwImageLayout;
	private HWItemTO hwItem;
	private HWItemDetailsDialog hwItemDetailDialog;

	public HWDetailsInfoTab(HWItemTO hwItem, HWItemDetailsDialog hwItemDetailDialog) {
		SpringContextHelper.inject(this);
		setHeightFull();
		this.hwItem = hwItem;
		this.hwItemDetailDialog = hwItemDetailDialog;
		init();
	}

	private String createPriceString(BigDecimal price) {
		if (price == null)
			return "-";
		return MoneyFormatter.format(price);
	}

	private String createWarrantyYearsString(Integer warrantyYears) {
		return new CZAmountFormatter("rok", "roky", "let").format(warrantyYears);
	}

	private void init() {
		HorizontalLayout itemLayout = new HorizontalLayout();
		itemLayout.setSpacing(true);
		itemLayout.setPadding(false);
		itemLayout.addClassName(UIUtils.TOP_MARGIN_CSS_CLASS);
		add(itemLayout);

		hwImageLayout = new VerticalLayout();
		hwImageLayout.setSpacing(true);
		hwImageLayout.setPadding(false);
		hwImageLayout.setDefaultHorizontalComponentAlignment(Alignment.CENTER);
		hwImageLayout.setWidth("200px");
		hwImageLayout.getStyle().set("height",
				"calc(" + UIUtils.BUTTON_SIZE_CSS_VAR + " + 200px + " + UIUtils.SPACING_CSS_VAR + ")");
		itemLayout.add(hwImageLayout);
		createHWImageOrUpload(hwItem);

		Div itemDetailsLayout = new Div();
		itemLayout.add(itemDetailsLayout);

		HorizontalLayout tags = new HorizontalLayout();
		tags.setSpacing(true);
		hwItem.getTypes().forEach(typeName -> {
			Button token = new Button(typeName);
			tags.add(token);
		});
		itemDetailsLayout.add(tags);

		Div rightPartLayout = new Div();
		itemDetailsLayout.add(rightPartLayout);

		GridLayout gridLayout = new GridLayout();
		gridLayout.addClassName(UIUtils.TOP_MARGIN_CSS_CLASS);
		rightPartLayout.add(gridLayout);

		gridLayout.add(new Strong("Stav"));
		gridLayout.add(new Strong("Získáno"));
		gridLayout.add(new Strong("Spravováno pro"));
		gridLayout.newRow();

		gridLayout.add(new Span(hwItem.getState().getName()));
		DateTimeFormatter format = DateTimeFormatter.ofPattern("d.M.yyyy");
		String purchDate = hwItem.getPurchaseDate() == null ? "-" : hwItem.getPurchaseDate().format(format);
		gridLayout.add(new Span(purchDate));
		gridLayout.add(new Span(StringUtils.isBlank(hwItem.getSupervizedFor()) ? "-" : hwItem.getSupervizedFor()));
		gridLayout.newRow();

		gridLayout.add(new Strong("Cena"));
		gridLayout.add(new Strong("Odepsáno"));
		gridLayout.add(new Strong("Záruka"));
		gridLayout.newRow();

		gridLayout.add(new Span(createPriceString(hwItem.getPrice())));
		String destrDate = hwItem.getDestructionDate() == null ? "-" : hwItem.getDestructionDate().format(format);
		gridLayout.add(new Span(destrDate));

		Div zarukaLayout = new Div();
		if (hwItem.getWarrantyYears() != null && hwItem.getWarrantyYears() > 0 && hwItem.getPurchaseDate() != null) {
			LocalDate endDate = hwItem.getPurchaseDate().plusYears(hwItem.getWarrantyYears());
			boolean isInWarranty = endDate.isAfter(LocalDate.now());
			Image emb = new Image(
					isInWarranty ? ImageIcon.TICK_16_ICON.createResource() : ImageIcon.DELETE_16_ICON.createResource(),
					"warranty");
			zarukaLayout.add(emb);
			emb.getStyle().set("margin-right", "5px").set("margin-bottom", "-3px");
			String zarukaContent = hwItem.getWarrantyYears() + " "
					+ createWarrantyYearsString(hwItem.getWarrantyYears()) + " (do " + endDate.format(format) + ")";
			zarukaLayout.add(zarukaContent);
		} else {
			zarukaLayout.add("-");
		}
		gridLayout.add(zarukaLayout);
		gridLayout.newRow();

		rightPartLayout.add(new Div(new Strong("Je součástí")));
		Div marginDiv = new Div();
		marginDiv.addClassName(UIUtils.TOP_MARGIN_CSS_CLASS);
		rightPartLayout.add(marginDiv);
		if (hwItem.getUsedIn() == null) {
			marginDiv.add(new Span("-"));
		} else {
			// Samotný button se stále roztahoval, bez ohledu na nastavený width
			Button usedInBtn = new Button(hwItem.getUsedIn().getName());
			usedInBtn.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);
			usedInBtn.addClickListener(e -> {
				hwItemDetailDialog.close();
				new HWItemDetailsDialog(hwItem.getUsedIn().getId()).open();
			});
			marginDiv.add(usedInBtn);
		}

		Div name = new Div(new Strong("Součásti"));
		name.addClassName(UIUtils.TOP_MARGIN_CSS_CLASS);
		add(name);

		List<HWItemOverviewTO> parts = hwService.getAllParts(hwItem.getId());
		Div partsContainer = new ContainerDiv();
		partsContainer.addClassName(UIUtils.TOP_MARGIN_CSS_CLASS);
		partsContainer.setHeight("200px");
		add(partsContainer);

		for (final HWItemOverviewTO part : parts) {
			Button partDetailBtn = new Button(part.getName());
			partDetailBtn.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);
			partDetailBtn.addClickListener(e -> {
				hwItemDetailDialog.close();
				HWItemTO detailTO = hwService.getHWItem(part.getId());
				new HWItemDetailsDialog(detailTO.getId()).open();
			});
			partsContainer.add(partDetailBtn);
		}

		HorizontalLayout operationsLayout = new HorizontalLayout();
		operationsLayout.setSpacing(false);
		operationsLayout.setJustifyContentMode(JustifyContentMode.BETWEEN);
		add(operationsLayout);

		ButtonLayout buttonLayout = new ButtonLayout();
		operationsLayout.add(buttonLayout);

		final Button fixBtn = new ModifyButton(e -> new HWItemDialog(hwItem) {
			private static final long serialVersionUID = -1397391593801030584L;

			@Override
			protected void onSuccess(HWItemTO dto) {
				hwItemDetailDialog.switchInfoTab();
			}
		}.open());
		buttonLayout.add(fixBtn);

		final Button deleteBtn = new DeleteButton(e -> new ConfirmDialog(
				"Opravdu smazat '" + hwItem.getName() + "' (budou smazány i servisní záznamy a údaje u součástí) ?",
				ev -> {
					try {
						hwService.deleteHWItem(hwItem.getId());
						hwItemDetailDialog.close();
					} catch (Exception ex) {
						new ErrorDialog("Nezdařilo se smazat vybranou položku").open();
					}
				}).open());
		buttonLayout.add(deleteBtn);

		CloseButton closeButton = new CloseButton(e -> hwItemDetailDialog.close());
		closeButton.addClassName(UIUtils.TOP_MARGIN_CSS_CLASS);
		operationsLayout.add(closeButton);
	}

	private void createHWImageOrUpload(final HWItemTO hwItem) {
		if (!tryCreateHWImage(hwItem))
			createHWItemImageUpload(hwItem);
	}

	/**
	 * Pokusí se získat ikonu HW
	 */
	private boolean tryCreateHWImage(final HWItemTO hwItem) {
		InputStream iconIs;
		iconIs = hwService.getHWItemIconFileInputStream(hwItem.getId());
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

		Button hwItemImageDetailBtn = new Button("Detail", e -> UI.getCurrent().getPage()
				.open(HWConfiguration.HW_PATH + "/" + hwItem.getId() + "/icon/" + hwItem.getName()));
		hwItemImageDetailBtn.setIcon(new Image(ImageIcon.SEARCH_16_ICON.createResource(), "detail"));

		Button hwItemImageDeleteBtn = new DeleteButton(
				e -> new ConfirmDialog("Opravdu smazat foto HW položky ?", ev -> {
					hwService.deleteHWItemIconFile(hwItem.getId());
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
		// https://vaadin.com/components/vaadin-upload/java-examples
		Button uploadButton = new Button("Upload");
		upload.setUploadButton(uploadButton);
		Span dropLabel = new Span("Drop");
		upload.setDropLabel(dropLabel);
		upload.setMaxFileSize(2000000);
		upload.setAcceptedFileTypes("image/jpg", "image/jpeg", "image/png");
		upload.addSucceededListener(e -> {
			try {
				// vytvoř miniaturu
				OutputStream bos = hwService.createHWItemIconOutputStream(e.getFileName(), hwItem.getId());
				IOUtils.copy(buffer.getInputStream(), bos);
				tryCreateHWImage(hwItem);
			} catch (IOException ex) {
				String err = "Nezdařilo se nahrát obrázek nápoje";
				logger.error(err, ex);
				UIUtils.showError(err);
			}
		});
		hwImageLayout.removeAll();
		HorizontalLayout hl = new HorizontalLayout();
		hl.setDefaultVerticalComponentAlignment(Alignment.CENTER);
		hl.add(upload);
		hl.setHeightFull();
		hwImageLayout.add(hl);
	}
}
