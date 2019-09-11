package cz.gattserver.grass3.songs.web;

import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.Component;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.VerticalLayout;

import cz.gattserver.grass3.server.GrassRequest;
import cz.gattserver.grass3.songs.model.interfaces.SongTO;
import cz.gattserver.grass3.ui.pages.template.OneColumnPage;
import cz.gattserver.web.common.server.URLIdentifierUtils;

public class SongsPage extends OneColumnPage {

	public SongsPage(GrassRequest request) {
		super(request);
	}

	@Override
	protected Component createColumnContent() {
		VerticalLayout layout = new VerticalLayout();
		layout.setSpacing(true);
		layout.setMargin(true);

		VerticalLayout marginLayout = new VerticalLayout();
		marginLayout.setMargin(new MarginInfo(false, true, true, true));
		marginLayout.addComponent(layout);

		TabSheet tabSheet = new TabSheet();
		layout.addComponent(tabSheet);

		ChordsTab ct = new ChordsTab(getRequest());
		SongTab st = new SongTab(getRequest(), tabSheet);
		ListTab lt = new ListTab(getRequest(), tabSheet);

		st.setChordsTab(ct).setListTab(lt).init();
		lt.setSongTab(st).init();

		tabSheet.addTab(lt, "Seznam");
		tabSheet.addTab(st, "Písnička");
		tabSheet.addTab(ct, "Akordy");

		tabSheet.addSelectedTabChangeListener((e) -> {
			boolean isListTab = e.getTabSheet().getSelectedTab().equals(lt);
			SongTO choosenSong = lt.getChoosenSong();
			if (isListTab && choosenSong != null) 
				lt.selectSong(lt.getChoosenSong().getId());
		});

		String token = getRequest().getAnalyzer().getNextPathToken();
		if (token != null) {
			if ("text".equals(token.toLowerCase())) {
				URLIdentifierUtils.URLIdentifier identifier = URLIdentifierUtils
						.parseURLIdentifier(getRequest().getAnalyzer().getNextPathToken());
				lt.chooseSong(identifier.getId(), true);
			} else if ("chord".equals(token.toLowerCase())) {
				tabSheet.setSelectedTab(ct);
				ct.selectChord(getRequest().getAnalyzer().getNextPathToken());
			}
		}

		return marginLayout;
	}
}
