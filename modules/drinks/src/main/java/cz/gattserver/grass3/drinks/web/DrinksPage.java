package cz.gattserver.grass3.drinks.web;

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

		TabSheet tabSheet = new TabSheet();
		layout.addComponent(tabSheet);

		DrinksTab tt = new DrinksTab(getRequest());
		tabSheet.addTab(tt, "Nápoje");

		String token = getRequest().getAnalyzer().getNextPathToken();
		if (token != null) {
			if ("drink".equals(token.toLowerCase())) {
				URLIdentifierUtils.URLIdentifier identifier = URLIdentifierUtils
						.parseURLIdentifier(getRequest().getAnalyzer().getNextPathToken());
				tabSheet.setSelectedTab(tt);
				tt.selectDrink(identifier.getId());
			}
		}

		return layout;
	}
}
