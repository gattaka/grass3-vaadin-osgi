package cz.gattserver.grass3.songs.web;

import com.vaadin.ui.Component;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.VerticalLayout;

import cz.gattserver.grass3.server.GrassRequest;
import cz.gattserver.grass3.ui.pages.template.OneColumnPage;
import cz.gattserver.web.common.server.URLIdentifierUtils;

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

		TextsTab tt = new TextsTab(getRequest());
		ChordsTab ct = new ChordsTab(getRequest());
		tabSheet.addTab(tt, "Písničky");
		tabSheet.addTab(ct, "Akordy");

		String token = getRequest().getAnalyzer().getNextPathToken();
		if (token != null) {
			URLIdentifierUtils.URLIdentifier identifier = URLIdentifierUtils
					.parseURLIdentifier(getRequest().getAnalyzer().getNextPathToken());
			if (identifier != null) {
				if ("text".equals(token.toLowerCase())) {
					tabSheet.setSelectedTab(tt);
					tt.selectSong(identifier.getId());
				} else if ("chord".equals(token.toLowerCase())) {
					tabSheet.setSelectedTab(ct);
					ct.selectChord(identifier.getId());
				}
			}
		}

		return layout;
	}
}
