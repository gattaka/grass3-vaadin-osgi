package cz.gattserver.grass3.songs.web;

import com.vaadin.ui.Component;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.VerticalLayout;

import cz.gattserver.grass3.server.GrassRequest;
import cz.gattserver.grass3.ui.pages.template.OneColumnPage;

public class SongsPage extends OneColumnPage {

	public SongsPage(GrassRequest request) {
		super(request);
	}

	@Override
	protected Component createContent() {
		VerticalLayout layout = new VerticalLayout();
		layout.setSpacing(true);
		layout.setMargin(true);

		TabSheet tabSheet = new TabSheet();
		layout.addComponent(tabSheet);

		tabSheet.addTab(new TextsTab(), "Písničky");
		tabSheet.addTab(new ChordsTab(), "Akordy");

		return layout;
	}
}
