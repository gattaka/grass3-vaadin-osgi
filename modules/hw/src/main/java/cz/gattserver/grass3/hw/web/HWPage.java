package cz.gattserver.grass3.hw.web;

import com.vaadin.ui.Component;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.VerticalLayout;

import cz.gattserver.grass3.pages.template.OneColumnPage;
import cz.gattserver.grass3.server.GrassRequest;

public class HWPage extends OneColumnPage {

	public HWPage(GrassRequest request) {
		super(request);
	}

	@Override
	protected Component createContent() {
		VerticalLayout layout = new VerticalLayout();
		layout.setSpacing(true);
		layout.setMargin(true);

		VerticalLayout marginlayout = new VerticalLayout();
		marginlayout.setMargin(true);
		layout.addComponent(marginlayout);

		TabSheet tabSheet = new TabSheet();
		marginlayout.addComponent(tabSheet);

		tabSheet.addTab(new HWItemsTab(), "Přehled");
		tabSheet.addTab(new HWTypesTab(), "Typy zařízení");

		return layout;
	}
}
