package cz.gattserver.grass3.drinks.web;

import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.Component;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.VerticalLayout;

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

		BeersTab tt = new BeersTab(getRequest());
		tabSheet.addTab(tt, "Piva");

		String token = getRequest().getAnalyzer().getNextPathToken();
		if (token != null) {
			if ("pivo".equals(token.toLowerCase())) {
				URLIdentifierUtils.URLIdentifier identifier = URLIdentifierUtils
						.parseURLIdentifier(getRequest().getAnalyzer().getNextPathToken());
				tabSheet.setSelectedTab(tt);
				tt.selectDrink(identifier.getId());
			}
		}

		return wrapperLayout;
	}
}
