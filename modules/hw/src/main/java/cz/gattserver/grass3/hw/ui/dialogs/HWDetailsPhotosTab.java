package cz.gattserver.grass3.hw.ui.dialogs;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MultiFileMemoryBuffer;
import com.vaadin.flow.server.StreamResource;

import cz.gattserver.grass3.hw.HWConfiguration;
import cz.gattserver.grass3.hw.interfaces.HWItemFileTO;
import cz.gattserver.grass3.hw.interfaces.HWItemTO;
import cz.gattserver.grass3.hw.service.HWService;
import cz.gattserver.grass3.ui.components.button.CloseButton;
import cz.gattserver.grass3.ui.components.button.DeleteButton;
import cz.gattserver.grass3.ui.components.button.DetailButton;
import cz.gattserver.grass3.ui.util.ButtonLayout;
import cz.gattserver.grass3.ui.util.ContainerDiv;
import cz.gattserver.grass3.ui.util.GridLayout;
import cz.gattserver.web.common.spring.SpringContextHelper;
import cz.gattserver.web.common.ui.window.ConfirmDialog;
import cz.gattserver.web.common.ui.window.ErrorDialog;

public class HWDetailsPhotosTab extends Div {

	private static final long serialVersionUID = 8602793883158440889L;

	private static final Logger logger = LoggerFactory.getLogger(HWDetailsPhotosTab.class);

	@Autowired
	private HWService hwService;

	private HWItemTO hwItem;
	private HWItemDetailsDialog hwItemDetailDialog;
	private ContainerDiv containerDiv;

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
				populateImages();
				hwItemDetailDialog.refreshTabLabels();
			} catch (IOException e) {
				String msg = "Nezdařilo se uložit obrázek";
				logger.error(msg, e);
				new ErrorDialog(msg).open();
			}
		});

		containerDiv = new ContainerDiv();
		containerDiv.setHeight("500px");
		add(containerDiv);
		add(upload);
		populateImages();

		HorizontalLayout operationsLayout = new HorizontalLayout();
		operationsLayout.setSpacing(false);
		operationsLayout.setJustifyContentMode(JustifyContentMode.BETWEEN);
		add(operationsLayout);

		ButtonLayout buttonLayout = new ButtonLayout();
		operationsLayout.add(buttonLayout);

		CloseButton closeButton = new CloseButton(e -> hwItemDetailDialog.close());
		closeButton.addClassName("top-margin");
		operationsLayout.add(closeButton);
	}

	private void populateImages() {
		containerDiv.removeAll();
		GridLayout gridLayout = new GridLayout();
		containerDiv.add(gridLayout);

		int counter = 0;
		for (HWItemFileTO item : hwService.getHWItemImagesFiles(hwItem.getId())) {
			if (counter == 0)
				gridLayout.newRow();
			counter = (counter + 1) % 4;

			Div itemDiv = new Div();
			itemDiv.getStyle().set("text-align", "center");
			itemDiv.setHeight("calc(var(--lumo-button-size) + 200px + var(--lumo-space-m))");
			gridLayout.add(itemDiv);

			Image img = new Image(
					new StreamResource(item.getName(),
							() -> hwService.getHWItemImagesFileInputStream(hwItem.getId(), item.getName())),
					item.getName());
			img.addClassName("thumbnail-200");
			itemDiv.add(img);

			Div buttonLayout = new Div();
			itemDiv.add(buttonLayout);

			DetailButton detailButton = new DetailButton(e -> UI.getCurrent().getPage()
					.open(HWConfiguration.HW_PATH + "/" + hwItem.getId() + "/img/" + item.getName()));
			detailButton.getStyle().set("margin-right", "var(--lumo-space-m)");
			Button delBtn = new DeleteButton(e -> new ConfirmDialog("Opravdu smazat foto HW položky ?", ev -> {
				hwService.deleteHWItemImagesFile(hwItem.getId(), item.getName());
				populateImages();
				hwItemDetailDialog.refreshTabLabels();
			}).open());
			buttonLayout.add(detailButton);
			buttonLayout.add(delBtn);
		}
	}
}