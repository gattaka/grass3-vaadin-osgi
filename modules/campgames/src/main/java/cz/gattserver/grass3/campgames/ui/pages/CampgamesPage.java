package cz.gattserver.grass3.campgames.ui.pages;

import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.Component;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.VerticalLayout;

import cz.gattserver.grass3.campgames.ui.CampgamesTab;
import cz.gattserver.grass3.campgames.ui.CampgameKeywordsTab;
import cz.gattserver.grass3.server.GrassRequest;
import cz.gattserver.grass3.ui.pages.template.OneColumnPage;

public class CampgamesPage extends OneColumnPage {

	public CampgamesPage(GrassRequest request) {
		super(request);
	}

	@Override
	protected Component createContent() {
		VerticalLayout layout = new VerticalLayout();
		layout.setSpacing(true);
		layout.setMargin(true);

		VerticalLayout marginlayout = new VerticalLayout();
		marginlayout.setMargin(new MarginInfo(false, true, true, true));
		layout.addComponent(marginlayout);

		TabSheet tabSheet = new TabSheet();
		marginlayout.addComponent(tabSheet);

		tabSheet.addTab(new CampgamesTab(getRequest()), "Přehled");
		tabSheet.addTab(new CampgameKeywordsTab(), "Klíčová slova");

		return layout;
	}
}
