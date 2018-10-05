package cz.gattserver.grass3.drinks.ui.pages;

import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.Component;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.VerticalLayout;

import cz.gattserver.grass3.drinks.ui.BeersTab;
import cz.gattserver.grass3.drinks.ui.RumTab;
import cz.gattserver.grass3.drinks.ui.WhiskeyTab;
import cz.gattserver.grass3.drinks.ui.WineTab;
import cz.gattserver.grass3.server.GrassRequest;
import cz.gattserver.grass3.ui.pages.template.OneColumnPage;
import cz.gattserver.web.common.server.URLIdentifierUtils;

public class DrinksPage extends OneColumnPage {

	public DrinksPage(GrassRequest request) {
		super(request);
	}

	@Override
	protected Component createContent() {
		VerticalLayout layout = new VerticalLayout();
		layout.setSpacing(true);
		layout.setMargin(true);

		VerticalLayout wrapperLayout = new VerticalLayout();
		layout.setMargin(new MarginInfo(false, true, true, true));
		wrapperLayout.addComponent(layout);

		TabSheet tabSheet = new TabSheet();
		layout.addComponent(tabSheet);

		BeersTab bt = new BeersTab(getRequest());
		tabSheet.addTab(bt, "Piva");
		RumTab rt = new RumTab(getRequest());
		tabSheet.addTab(rt, "Rumy");
		WhiskeyTab wht = new WhiskeyTab(getRequest());
		tabSheet.addTab(wht, "Whiskey");
		WineTab wit = new WineTab(getRequest());
		tabSheet.addTab(wit, "Vína");

		String token = getRequest().getAnalyzer().getNextPathToken();
		if (token != null) {
			URLIdentifierUtils.URLIdentifier identifier = URLIdentifierUtils
					.parseURLIdentifier(getRequest().getAnalyzer().getNextPathToken());
			switch (token.toLowerCase()) {
			case "beer":
				tabSheet.setSelectedTab(bt);
				bt.selectDrink(identifier.getId());
				break;
			case "rum":
				tabSheet.setSelectedTab(rt);
				rt.selectDrink(identifier.getId());
				break;
			case "whiskey":
				tabSheet.setSelectedTab(wht);
				wht.selectDrink(identifier.getId());
				break;
			case "wine":
				tabSheet.setSelectedTab(wit);
				wit.selectDrink(identifier.getId());
				break;
			}

		}

		return wrapperLayout;
	}
}
