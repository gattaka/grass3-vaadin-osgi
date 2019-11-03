package cz.gattserver.grass3.hw.ui.dialogs;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.Grid.SelectionMode;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.renderer.IconRenderer;
import com.vaadin.flow.server.StreamResource;

import cz.gattserver.common.util.CZAmountFormatter;
import cz.gattserver.common.util.MoneyFormatter;
import cz.gattserver.grass3.hw.HWConfiguration;
import cz.gattserver.grass3.hw.interfaces.HWItemOverviewTO;
import cz.gattserver.grass3.hw.interfaces.HWItemTO;
import cz.gattserver.grass3.hw.service.HWService;
import cz.gattserver.grass3.hw.ui.HWUIUtils;
import cz.gattserver.grass3.ui.components.OperationsLayout;
import cz.gattserver.grass3.ui.components.button.DeleteButton;
import cz.gattserver.grass3.ui.components.button.ModifyButton;
import cz.gattserver.grass3.ui.util.ContainerDiv;
import cz.gattserver.grass3.ui.util.GridLayout;
import cz.gattserver.grass3.ui.util.UIUtils;
import cz.gattserver.web.common.spring.SpringContextHelper;
import cz.gattserver.web.common.ui.ImageIcon;
import cz.gattserver.web.common.ui.LinkButton;
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

	private String createShortName(String name) {
		int maxLength = 50;
		if (name.length() <= maxLength)
			return name;
		return name.substring(0, maxLength / 2 - 3) + "..." + name.substring(name.length() - maxLength / 2);
	}

	private void init() {
		setWidth("1000px");

		HorizontalLayout tags = new HorizontalLayout();
		tags.setSpacing(true);
		hwItem.getTypes().forEach(typeName -> {
			Button token = new Button(typeName);
			tags.add(token);
		});
		add(tags);

		HorizontalLayout outerLayout = new HorizontalLayout();
		outerLayout.setSpacing(true);
		outerLayout.setPadding(false);
		outerLayout.addClassName(UIUtils.TOP_MARGIN_CSS_CLASS);
		add(outerLayout);

		VerticalLayout leftPartLayout = new VerticalLayout();
		leftPartLayout.setSpacing(true);
		leftPartLayout.setPadding(false);
		outerLayout.add(leftPartLayout);

		HorizontalLayout itemLayout = new HorizontalLayout();
		itemLayout.setSpacing(true);
		itemLayout.setPadding(false);
		itemLayout.addClassName(UIUtils.TOP_MARGIN_CSS_CLASS);
		leftPartLayout.add(itemLayout);

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

		Div rightPartLayout = new Div();
		itemDetailsLayout.add(rightPartLayout);

		GridLayout gridLayout1 = new GridLayout();
		rightPartLayout.add(gridLayout1);

		gridLayout1.add(new Strong("Stav"));
		gridLayout1.add(new Strong("Získáno"));
		gridLayout1.add(new Strong("Spravováno pro"));
		gridLayout1.newRow();

		Div stateValue = new Div(new Text(hwItem.getState().getName()));
		stateValue.setMinWidth("100px");
		gridLayout1.add(stateValue);

		DateTimeFormatter format = DateTimeFormatter.ofPattern("d.M.yyyy");
		Div purchDateValue = new Div(
				new Text(hwItem.getPurchaseDate() == null ? "-" : hwItem.getPurchaseDate().format(format)));
		purchDateValue.setMinWidth("100px");
		gridLayout1.add(purchDateValue);
		gridLayout1.add(new Span(StringUtils.isBlank(hwItem.getSupervizedFor()) ? "-" : hwItem.getSupervizedFor()));

		GridLayout gridLayout2 = new GridLayout();
		rightPartLayout.add(gridLayout2);

		gridLayout2.add(new Strong("Cena"));
		gridLayout2.add(new Strong("Záruka"));
		gridLayout2.newRow();

		Div priceValue = new Div(new Text(createPriceString(hwItem.getPrice())));
		priceValue.setMinWidth("100px");
		gridLayout2.add(priceValue);

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
		gridLayout2.add(zarukaLayout);

		rightPartLayout.add(new Div(new Strong("Je součástí")));
		Div marginDiv = new Div();
		marginDiv.addClassName(UIUtils.TOP_MARGIN_CSS_CLASS);
		rightPartLayout.add(marginDiv);
		if (hwItem.getUsedIn() == null) {
			marginDiv.add(new Span("-"));
		} else {
			// Samotný button se stále roztahoval, bez ohledu na nastavený width
			Button usedInBtn = new LinkButton(hwItem.getUsedIn().getName(), e -> {
				hwItemDetailDialog.close();
				new HWItemDetailsDialog(hwItem.getUsedIn().getId()).open();
			});
			marginDiv.add(usedInBtn);
		}

		Div name = new Div(new Strong("Součásti"));
		name.addClassName(UIUtils.TOP_MARGIN_CSS_CLASS);
		leftPartLayout.add(name);

		// Tabulka HW
		Grid<HWItemOverviewTO> grid = new Grid<>();
		grid.addClassName(UIUtils.TOP_MARGIN_CSS_CLASS);
		grid.addThemeVariants(GridVariant.LUMO_COLUMN_BORDERS, GridVariant.LUMO_ROW_STRIPES, GridVariant.LUMO_COMPACT);
		grid.setSelectionMode(SelectionMode.SINGLE);
		grid.setWidthFull();
		grid.setHeightFull();

		grid.addColumn(new IconRenderer<HWItemOverviewTO>(c -> {
			ImageIcon ii = HWUIUtils.chooseImageIcon(c);
			if (ii != null) {
				Image img = new Image(ii.createResource(), c.getState().getName());
				img.getStyle().set("margin-bottom", "-4px");
				return img;
			} else {
				return new Span();
			}
		}, c -> "")).setFlexGrow(0).setWidth("26px").setHeader("");

		grid.addColumn(
				new ComponentRenderer<Button, HWItemOverviewTO>(c -> new LinkButton(createShortName(c.getName()), e -> {
					hwItemDetailDialog.close();
					HWItemTO detailTO = hwService.getHWItem(c.getId());
					new HWItemDetailsDialog(detailTO.getId()).open();
				}))).setHeader("Název").setFlexGrow(100);

		// kontrola na null je tady jenom proto, aby při selectu (kdy se udělá
		// nový objekt a dá se mu akorát ID, které se porovnává) aplikace
		// nespadla na NPE -- což je trochu zvláštní, protože ve skutečnosti
		// žádný majetek nemá stav null.
		grid.addColumn(hw -> hw.getState() == null ? "" : hw.getState().getName()).setHeader("Stav").setWidth("110px")
				.setFlexGrow(0);

		grid.setItems(hwService.getAllParts(hwItem.getId()));

		leftPartLayout.add(grid);

		Div descriptionWrapper = new Div();
		descriptionWrapper.add(new Strong("Popis"));
		descriptionWrapper.setWidth("700px");
		outerLayout.add(descriptionWrapper);

		Div descriptionDiv = new ContainerDiv();
		descriptionDiv.addClassName(UIUtils.TOP_MARGIN_CSS_CLASS);
		descriptionDiv.setHeight("500px");
		descriptionDiv.setText(hwItem.getDescription());
		descriptionWrapper.add(descriptionDiv);

		OperationsLayout operationsLayout = new OperationsLayout(e -> hwItemDetailDialog.close());
		operationsLayout.addClassName(UIUtils.TOP_MARGIN_CSS_CLASS);
		operationsLayout.setSpacing(false);
		operationsLayout.setJustifyContentMode(JustifyContentMode.BETWEEN);
		add(operationsLayout);

		final Button fixBtn = new ModifyButton(e -> new HWItemDialog(hwItem) {
			private static final long serialVersionUID = -1397391593801030584L;

			@Override
			protected void onSuccess(HWItemTO dto) {
				hwItemDetailDialog.switchInfoTab();
			}
		}.open());
		operationsLayout.add(fixBtn);

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
		operationsLayout.add(deleteBtn);
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
		hwImageLayout.getStyle().set("border", "");

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
		hwImageLayout.getStyle().set("border", "1px solid lightgray");
		HorizontalLayout hl = new HorizontalLayout();
		hl.setDefaultVerticalComponentAlignment(Alignment.CENTER);
		hl.add(upload);
		hl.setHeightFull();
		hwImageLayout.add(hl);
	}
}
