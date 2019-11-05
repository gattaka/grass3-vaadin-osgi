package cz.gattserver.grass3.hw.ui.dialogs;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;

import cz.gattserver.grass3.hw.interfaces.HWItemTO;
import cz.gattserver.grass3.hw.service.HWService;
import cz.gattserver.grass3.ui.util.UIUtils;
import cz.gattserver.web.common.spring.SpringContextHelper;

public class HWItemDetailsDialog extends Dialog {

	private static final long serialVersionUID = -6773027334692911384L;

	private transient HWService hwService;

	private Tabs tabs;
	private Tab infoTab;
	private Tab serviceNotesTab;
	private Tab photosTab;
	private Tab docsTab;

	private Div tabLayout;

	private HWItemTO hwItem;
	private Long hwItemId;

	public HWItemDetailsDialog(Long hwItemId) {
		this.hwItemId = hwItemId;
		this.hwItem = getHWService().getHWItem(hwItemId);

		Div nameDiv = new Div(new Text(hwItem.getName()));
		nameDiv.getStyle().set("font-size", "15px").set("margin-bottom", "var(--lumo-space-m)")
				.set("font-weight", "bold").set("margin-top", "calc(var(--lumo-space-m) / -2)");
		add(nameDiv);

		infoTab = new Tab("Info");
		serviceNotesTab = new Tab(createServiceNotesTabLabel());
		photosTab = new Tab(createPhotosTabLabel());
		docsTab = new Tab(createDocsTabLabel());

		tabs = new Tabs();
		tabs.add(infoTab, serviceNotesTab, photosTab, docsTab);
		add(tabs);

		tabLayout = new Div();
		tabLayout.setSizeFull();
		tabLayout.addClassName(UIUtils.TOP_MARGIN_CSS_CLASS);
		add(tabLayout);

		tabs.addSelectedChangeListener(e -> {
			switch (tabs.getSelectedIndex()) {
			default:
			case 0:
				switchInfoTab();
				break;
			case 1:
				switchServiceNotesTab();
				break;
			case 2:
				switchPhotosTab();
				break;
			case 3:
				switchDocsTab();
				break;
			}
		});

		switchInfoTab();
	}

	public HWItemTO refreshItem() {
		this.hwItem = getHWService().getHWItem(hwItemId);
		refreshTabLabels();
		return hwItem;
	}

	public void refreshTabLabels() {
		serviceNotesTab.setLabel(createServiceNotesTabLabel());
		photosTab.setLabel(createPhotosTabLabel());
		docsTab.setLabel(createDocsTabLabel());
	}

	private String createServiceNotesTabLabel() {
		return "Záznamy (" + hwItem.getServiceNotes().size() + ")";
	}

	private String createPhotosTabLabel() {
		return "Fotografie (" + getHWService().getHWItemImagesFilesCount(hwItemId) + ")";
	}

	private String createDocsTabLabel() {
		return "Dokumentace (" + getHWService().getHWItemDocumentsFilesCount(hwItemId) + ")";
	}

	private HWService getHWService() {
		if (hwService == null)
			hwService = SpringContextHelper.getBean(HWService.class);
		return hwService;
	}

	public void switchInfoTab() {
		tabLayout.removeAll();
		tabLayout.add(new HWDetailsInfoTab(hwItem, this));
		tabs.setSelectedTab(infoTab);
	}

	public void switchServiceNotesTab() {
		tabLayout.removeAll();
		tabLayout.add(new HWDetailsServiceNotesTab(hwItem, this));
		tabs.setSelectedTab(serviceNotesTab);
	}

	private void switchPhotosTab() {
		tabLayout.removeAll();
		tabLayout.add(new HWDetailsPhotosTab(hwItem, this));
		tabs.setSelectedTab(photosTab);
	}

	private void switchDocsTab() {
		tabLayout.removeAll();
		tabLayout.add(new HWDetailsDocsTab(hwItem, this));
		tabs.setSelectedTab(docsTab);
	}

}