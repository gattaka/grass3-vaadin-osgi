package cz.gattserver.grass3.drinks.ui.pages;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.WildcardParameter;

import cz.gattserver.grass3.drinks.ui.BeersTab;
import cz.gattserver.grass3.drinks.ui.RumTab;
import cz.gattserver.grass3.drinks.ui.WhiskeyTab;
import cz.gattserver.grass3.drinks.ui.WineTab;
import cz.gattserver.grass3.ui.pages.template.OneColumnPage;
import cz.gattserver.web.common.server.URLIdentifierUtils;

@Route("drinks")
@PageTitle("Nápoje")
public class DrinksPage extends OneColumnPage implements HasUrlParameter<String> {

	private static final long serialVersionUID = 2066137985312535506L;

	private Tabs tabSheet;

	private Tab beerTab;
	private Tab rumTab;
	private Tab whiskeyTab;
	private Tab wineTab;

	private Div pageLayout;

	private String tabParam;
	private String itemParam;

	@Override
	public void setParameter(BeforeEvent event, @WildcardParameter String parameter) {
		String[] chunks = parameter.split("/");
		if (chunks.length > 0)
			this.tabParam = chunks[0];
		if (chunks.length > 1)
			this.itemParam = chunks[1];

		init();
	}

	private BeersTab switchBeersTab() {
		pageLayout.removeAll();
		BeersTab tab = new BeersTab();
		pageLayout.add(tab);
		tabSheet.setSelectedTab(beerTab);
		return tab;
	}

	private RumTab switchRumTab() {
		pageLayout.removeAll();
		RumTab tab = new RumTab();
		pageLayout.add(tab);
		tabSheet.setSelectedTab(rumTab);
		return tab;
	}

	private WhiskeyTab switchWhiskeyTab() {
		pageLayout.removeAll();
		WhiskeyTab tab = new WhiskeyTab();
		pageLayout.add(tab);
		tabSheet.setSelectedTab(whiskeyTab);
		return tab;
	}

	private WineTab switchWineTab() {
		pageLayout.removeAll();
		WineTab tab = new WineTab();
		pageLayout.add(tab);
		tabSheet.setSelectedTab(wineTab);
		return tab;
	}

	@Override
	protected void createColumnContent(Div layout) {
		tabSheet = new Tabs();
		layout.add(tabSheet);

		beerTab = new Tab("Piva");
		rumTab = new Tab("Rumy");
		whiskeyTab = new Tab("Whiskey");
		wineTab = new Tab("Vína");
		tabSheet.add(beerTab, rumTab, whiskeyTab, wineTab);

		pageLayout = new Div();
		layout.add(pageLayout);

		tabSheet.addSelectedChangeListener(e -> {
			pageLayout.removeAll();
			switch (tabSheet.getSelectedIndex()) {
			default:
			case 0:
				switchBeersTab();
				break;
			case 1:
				switchRumTab();
				break;
			case 2:
				switchWhiskeyTab();
				break;
			case 3:
				switchWineTab();
				break;
			}
		});

		if (tabParam != null) {
			Long itemId = null;
			if (itemParam != null) {
				URLIdentifierUtils.URLIdentifier identifier = URLIdentifierUtils.parseURLIdentifier(itemParam);
				itemId = identifier.getId();
			}
			switch (tabParam.toLowerCase()) {
			default:
			case "beer":
				BeersTab beersTab = switchBeersTab();
				if (itemId != null)
					beersTab.selectDrink(itemId);
				break;
			case "rum":
				RumTab rumTab = switchRumTab();
				if (itemId != null)
					rumTab.selectDrink(itemId);
				break;
			case "whiskey":
				WhiskeyTab whiskeyTab = switchWhiskeyTab();
				if (itemId != null)
					whiskeyTab.selectDrink(itemId);
				break;
			case "wine":
				WineTab wineTab = switchWineTab();
				if (itemId != null)
					wineTab.selectDrink(itemId);
				break;
			}
		} else {
			switchBeersTab();
		}
	}
}
