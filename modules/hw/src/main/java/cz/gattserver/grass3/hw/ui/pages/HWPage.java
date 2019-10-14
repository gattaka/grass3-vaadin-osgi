package cz.gattserver.grass3.hw.ui.pages;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.router.Route;

import cz.gattserver.grass3.hw.ui.HWItemsTab;
import cz.gattserver.grass3.hw.ui.HWTypesTab;
import cz.gattserver.grass3.ui.pages.template.OneColumnPage;

@Route("hw")
public class HWPage extends OneColumnPage {

	private static final long serialVersionUID = 3983638941237624740L;

	private Tabs tabSheet;

	private Tab overviewTab;
	private Tab typesTab;

	private Div pageLayout;

	public HWPage() {
		init();
	}

	@Override
	protected void createColumnContent(Div layout) {
		tabSheet = new Tabs();
		layout.add(tabSheet);

		overviewTab = new Tab("Přehled");
		typesTab = new Tab("Typy zařízení");
		tabSheet.add(overviewTab, typesTab);

		pageLayout = new Div();
		layout.add(pageLayout);

		tabSheet.addSelectedChangeListener(e -> {
			pageLayout.removeAll();
			switch (tabSheet.getSelectedIndex()) {
			default:
			case 0:
				switchOverviewTab();
				break;
			case 1:
				switchTypesTab();
				break;
			}
		});
	}

	private HWItemsTab switchOverviewTab() {
		pageLayout.removeAll();
		HWItemsTab tab = new HWItemsTab();
		pageLayout.add(tab);
		tabSheet.setSelectedTab(overviewTab);
		return tab;
	}

	private HWTypesTab switchTypesTab() {
		pageLayout.removeAll();
		HWTypesTab tab = new HWTypesTab();
		pageLayout.add(tab);
		tabSheet.setSelectedTab(typesTab);
		return tab;
	}
}
