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
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MultiFileMemoryBuffer;
import com.vaadin.flow.data.renderer.LocalDateTimeRenderer;
import com.vaadin.flow.data.renderer.TextRenderer;

import cz.gattserver.grass3.hw.HWConfiguration;
import cz.gattserver.grass3.hw.interfaces.HWItemFileTO;
import cz.gattserver.grass3.hw.interfaces.HWItemTO;
import cz.gattserver.grass3.hw.service.HWService;
import cz.gattserver.grass3.ui.components.OperationsLayout;
import cz.gattserver.grass3.ui.components.button.DeleteGridButton;
import cz.gattserver.grass3.ui.components.button.GridButton;
import cz.gattserver.grass3.ui.pages.template.GrassPage;
import cz.gattserver.grass3.ui.util.UIUtils;
import cz.gattserver.web.common.spring.SpringContextHelper;
import cz.gattserver.web.common.ui.ImageIcon;
import cz.gattserver.web.common.ui.window.ErrorDialog;

public class HWDetailsDocsTab extends Div {

	private static final long serialVersionUID = 8602793883158440889L;

	private static final Logger logger = LoggerFactory.getLogger(HWDetailsDocsTab.class);

	@Autowired
	private HWService hwService;

	private HWItemTO hwItem;
	private HWItemDetailsDialog hwItemDetailDialog;
	private Grid<HWItemFileTO> docsGrid;

	public HWDetailsDocsTab(HWItemTO hwItem, HWItemDetailsDialog hwItemDetailDialog) {
		SpringContextHelper.inject(this);
		this.hwItem = hwItem;
		this.hwItemDetailDialog = hwItemDetailDialog;
		init();
	}

	private void populateDocsGrid() {
		docsGrid.setItems(hwService.getHWItemDocumentsFiles(hwItem.getId()));
		docsGrid.getDataProvider().refreshAll();
	}

	private void downloadDocument(HWItemFileTO item) {
		UI.getCurrent().getPage().executeJs("window.open('" + GrassPage.getContextPath() + "/" + HWConfiguration.HW_PATH
				+ "/" + hwItem.getId() + "/doc/" + item.getName() + "', '_blank');");
	}

	private void init() {
		docsGrid = new Grid<>();
		docsGrid.setWidthFull();
		UIUtils.applyGrassDefaultStyle(docsGrid);
		docsGrid.addColumn(new TextRenderer<HWItemFileTO>(HWItemFileTO::getName)).setHeader("Název");
		docsGrid.addColumn(new LocalDateTimeRenderer<HWItemFileTO>(HWItemFileTO::getLastModified, "d.MM.yyyy HH:mm"))
				.setKey("datum").setHeader("Datum");
		docsGrid.addColumn(new TextRenderer<HWItemFileTO>(HWItemFileTO::getSize)).setHeader("Velikost")
				.setTextAlign(ColumnTextAlign.END);
		add(docsGrid);

		populateDocsGrid();

		MultiFileMemoryBuffer buffer = new MultiFileMemoryBuffer();

		Upload upload = new Upload(buffer);
		upload.addClassName(UIUtils.TOP_MARGIN_CSS_CLASS);
		upload.addSucceededListener(event -> {
			try {
				hwService.saveDocumentsFile(buffer.getInputStream(event.getFileName()), event.getFileName(),
						hwItem.getId());
				// refresh listu
				populateDocsGrid();
				hwItemDetailDialog.refreshTabLabels();
			} catch (IOException e) {
				String msg = "Nezdařilo se uložit soubor";
				logger.error(msg, e);
				new ErrorDialog(msg).open();
			}
		});

		add(upload);

		docsGrid.addItemClickListener(e -> {
			if (e.getClickCount() > 1)
				downloadDocument(e.getItem());
		});

		OperationsLayout operationsLayout = new OperationsLayout(e -> hwItemDetailDialog.close());
		add(operationsLayout);

		GridButton<HWItemFileTO> downloadBtn = new GridButton<>("Stáhnout",
				set -> downloadDocument(set.iterator().next()), docsGrid);
		downloadBtn.setEnableResolver(set -> set.size() == 1);
		downloadBtn.setIcon(new Image(ImageIcon.DOWN_16_ICON.createResource(), "Stáhnout"));
		operationsLayout.add(downloadBtn);

		Button deleteBtn = new DeleteGridButton<>("Smazat záznam", items -> {
			HWItemFileTO item = items.iterator().next();
			hwService.deleteHWItemDocumentsFile(hwItem.getId(), item.getName());
			populateDocsGrid();
			hwItemDetailDialog.refreshTabLabels();
		}, docsGrid);
		operationsLayout.add(deleteBtn);
	}

}