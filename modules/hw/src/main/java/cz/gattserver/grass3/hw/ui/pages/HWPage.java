package cz.gattserver.grass3.hw.ui.pages;

import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.Component;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.VerticalLayout;

import cz.gattserver.grass3.hw.ui.HWItemsTab;
import cz.gattserver.grass3.hw.ui.HWTypesTab;
import cz.gattserver.grass3.server.GrassRequest;
import cz.gattserver.grass3.ui.pages.template.OneColumnPage;

public class HWPage extends OneColumnPage {

	public HWPage(GrassRequest request) {
		super(request);
	}

	@Override
	protected Component createColumnContent() {
		VerticalLayout layout = new VerticalLayout();
		layout.setSpacing(true);
		layout.setPadding(true);

		VerticalLayout marginlayout = new VerticalLayout();
		marginlayout.setPadding(new MarginInfo(false, true, true, true));
		layout.addComponent(marginlayout);

		TabSheet tabSheet = new TabSheet();
		marginlayout.addComponent(tabSheet);

		tabSheet.addTab(new HWItemsTab(getRequest()), "Přehled");
		tabSheet.addTab(new HWTypesTab(), "Typy zařízení");

		return layout;
	}
}
