package cz.gattserver.grass3.hw.ui.dialogs;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MultiFileMemoryBuffer;
import com.vaadin.flow.data.renderer.LocalDateTimeRenderer;
import com.vaadin.flow.data.renderer.TextRenderer;

import cz.gattserver.grass3.hw.HWConfiguration;
import cz.gattserver.grass3.hw.interfaces.HWItemFileTO;
import cz.gattserver.grass3.hw.interfaces.HWItemTO;
import cz.gattserver.grass3.hw.service.HWService;
import cz.gattserver.grass3.stlviewer.STLViewer;
import cz.gattserver.grass3.ui.components.OperationsLayout;
import cz.gattserver.grass3.ui.components.button.DeleteGridButton;
import cz.gattserver.grass3.ui.components.button.GridButton;
import cz.gattserver.grass3.ui.util.UIUtils;
import cz.gattserver.web.common.spring.SpringContextHelper;
import cz.gattserver.web.common.ui.ImageIcon;
import cz.gattserver.web.common.ui.window.ErrorDialog;

public class HWDetailsPrint3dTab extends Div {

	private static final long serialVersionUID = 8602793883158440889L;

	private static final Logger logger = LoggerFactory.getLogger(HWDetailsPrint3dTab.class);

	@Autowired
	private HWService hwService;

	private HWItemTO hwItem;
	private HWItemDetailsDialog hwItemDetailDialog;
	private Grid<HWItemFileTO> print3dGrid;

	private STLViewer stlViewer;

	public HWDetailsPrint3dTab(HWItemTO hwItem, HWItemDetailsDialog hwItemDetailDialog) {
		SpringContextHelper.inject(this);
		this.hwItem = hwItem;
		this.hwItemDetailDialog = hwItemDetailDialog;
		init();
	}

	private void populatePrint3dGrid() {
		print3dGrid.setItems(hwService.getHWItemPrint3dFiles(hwItem.getId()));
		print3dGrid.getDataProvider().refreshAll();
	}

	private String getFileURL(HWItemFileTO item) {
		return UIUtils.getContextPath() + "/" + HWConfiguration.HW_PATH + "/" + hwItem.getId() + "/print3d/"
				+ item.getName();
	}

	private void downloadPrint3d(HWItemFileTO item) {
		UI.getCurrent().getPage().executeJs("window.open('" + getFileURL(item) + "', '_blank');");
	}

	private void init() {
		print3dGrid = new Grid<>();
		print3dGrid.setWidthFull();
		UIUtils.applyGrassDefaultStyle(print3dGrid);
		print3dGrid.addColumn(new TextRenderer<HWItemFileTO>(HWItemFileTO::getName)).setHeader("Název");
		print3dGrid.addColumn(new LocalDateTimeRenderer<HWItemFileTO>(HWItemFileTO::getLastModified, "d.MM.yyyy HH:mm"))
				.setKey("datum").setHeader("Datum");
		print3dGrid.addColumn(new TextRenderer<HWItemFileTO>(HWItemFileTO::getSize)).setHeader("Velikost")
				.setTextAlign(ColumnTextAlign.END);

		HorizontalLayout layout = new HorizontalLayout(print3dGrid, stlViewer);
		layout.setPadding(false);
		layout.setSpacing(true);
		add(layout);

		populatePrint3dGrid();

		MultiFileMemoryBuffer buffer = new MultiFileMemoryBuffer();

		Upload upload = new Upload(buffer);
		upload.addClassName(UIUtils.TOP_MARGIN_CSS_CLASS);
		upload.addSucceededListener(event -> {
			try {
				hwService.savePrint3dFile(buffer.getInputStream(event.getFileName()), event.getFileName(),
						hwItem.getId());
				// refresh listu
				populatePrint3dGrid();
				hwItemDetailDialog.refreshTabLabels();
			} catch (IOException e) {
				String msg = "Nezdařilo se uložit soubor";
				logger.error(msg, e);
				new ErrorDialog(msg).open();
			}
		});

		add(upload);

		print3dGrid.addItemClickListener(e -> {
			if (e.getClickCount() > 1)
				downloadPrint3d(e.getItem());
		});

		print3dGrid.addSelectionListener(item -> {
			if (!item.getFirstSelectedItem().isPresent())
				return;
			HWItemFileTO to = item.getFirstSelectedItem().get();
			stlViewer.show(getFileURL(to));
		});

		OperationsLayout operationsLayout = new OperationsLayout(e -> hwItemDetailDialog.close());
		add(operationsLayout);

		GridButton<HWItemFileTO> downloadBtn = new GridButton<>("Stáhnout",
				set -> downloadPrint3d(set.iterator().next()), print3dGrid);
		downloadBtn.setEnableResolver(set -> set.size() == 1);
		downloadBtn.setIcon(new Image(ImageIcon.DOWN_16_ICON.createResource(), "Stáhnout"));
		operationsLayout.add(downloadBtn);

		Button deleteBtn = new DeleteGridButton<>("Smazat záznam", items -> {
			HWItemFileTO item = items.iterator().next();
			hwService.deleteHWItemPrint3dFile(hwItem.getId(), item.getName());
			populatePrint3dGrid();
			hwItemDetailDialog.refreshTabLabels();
		}, print3dGrid);
		operationsLayout.add(deleteBtn);
	}

}