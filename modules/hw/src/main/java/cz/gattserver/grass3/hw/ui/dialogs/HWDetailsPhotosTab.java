package cz.gattserver.grass3.hw.ui.dialogs;

import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MultiFileMemoryBuffer;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.renderer.IconRenderer;
import com.vaadin.flow.data.renderer.TextRenderer;
import com.vaadin.flow.server.StreamResource;

import cz.gattserver.grass3.hw.HWConfiguration;
import cz.gattserver.grass3.hw.interfaces.HWItemFileTO;
import cz.gattserver.grass3.hw.interfaces.HWItemTO;
import cz.gattserver.grass3.hw.service.HWService;
import cz.gattserver.grass3.ui.components.button.CloseButton;
import cz.gattserver.grass3.ui.components.button.DeleteGridButton;
import cz.gattserver.grass3.ui.util.ButtonLayout;
import cz.gattserver.web.common.spring.SpringContextHelper;
import cz.gattserver.web.common.ui.window.ErrorDialog;

public class HWDetailsPhotosTab extends Div {

	private static final long serialVersionUID = 8602793883158440889L;

	private static final Logger logger = LoggerFactory.getLogger(HWDetailsPhotosTab.class);

	@Autowired
	private HWService hwService;

	private HWItemTO hwItem;
	private HWItemDetailsDialog hwItemDetailDialog;
	private Grid<HWItemFileTO> grid;

	public HWDetailsPhotosTab(HWItemTO hwItem, HWItemDetailsDialog hwItemDetailDialog) {
		SpringContextHelper.inject(this);
		this.hwItem = hwItem;
		this.hwItemDetailDialog = hwItemDetailDialog;
		init();
	}

	private void init() {
		MultiFileMemoryBuffer buffer = new MultiFileMemoryBuffer();

		Upload upload = new Upload(buffer);
		upload.addClassName("top-margin");
		upload.setAcceptedFileTypes("image/jpeg", "image/png", "image/gif");
		upload.addSucceededListener(event -> {
			try {
				hwService.saveImagesFile(buffer.getInputStream(event.getFileName()), event.getFileName(), hwItem);
				populateGrid();
				hwItemDetailDialog.refreshTabLabels();
			} catch (IOException e) {
				String msg = "Nezdařilo se uložit obrázek";
				logger.error(msg, e);
				new ErrorDialog(msg).open();
			}
		});

		createImagesList(upload);
		add(upload);

		HorizontalLayout operationsLayout = new HorizontalLayout();
		operationsLayout.setSpacing(false);
		operationsLayout.setJustifyContentMode(JustifyContentMode.BETWEEN);
		add(operationsLayout);

		ButtonLayout buttonLayout = new ButtonLayout();
		operationsLayout.add(buttonLayout);

		Button deleteBtn = new DeleteGridButton<>("Smazat záznam", items -> {
			HWItemFileTO item = items.iterator().next();
			hwService.deleteHWItemImagesFile(hwItem.getId(), item.getName());
			populateGrid();
			hwItemDetailDialog.refreshTabLabels();
		}, grid);
		buttonLayout.add(deleteBtn);

		CloseButton closeButton = new CloseButton(e -> hwItemDetailDialog.close());
		closeButton.addClassName("top-margin");
		operationsLayout.add(closeButton);
	}

	private void populateGrid() {
		List<HWItemFileTO> items = hwService.getHWItemImagesFiles(hwItem.getId());
		grid.setItems(items);
	}

	private void createImagesList(Upload upload) {
		grid = new Grid<>();
		grid.setWidthFull();
		grid.addThemeVariants(GridVariant.LUMO_COLUMN_BORDERS, GridVariant.LUMO_ROW_STRIPES, GridVariant.LUMO_COMPACT);
		grid.addClassName("top-margin");

		add(grid);

		grid.addColumn(new IconRenderer<HWItemFileTO>(to -> {
			Image img = new Image(
					new StreamResource(to.getName(),
							() -> hwService.getHWItemImagesFileInputStream(hwItem.getId(), to.getName())),
					to.getName());
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

		grid.addColumn(new TextRenderer<>(HWItemFileTO::getSize)).setHeader("Velikost")
				.setTextAlign(ColumnTextAlign.END).setFlexGrow(0).setWidth("80px");

		populateGrid();
	}
}