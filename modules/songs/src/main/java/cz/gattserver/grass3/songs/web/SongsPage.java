package cz.gattserver.grass3.songs.web;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.router.Route;

import cz.gattserver.grass3.songs.model.interfaces.SongTO;
import cz.gattserver.grass3.ui.pages.template.OneColumnPage;
import cz.gattserver.web.common.server.URLIdentifierUtils;

@Route("songs")
public class SongsPage extends OneColumnPage {

	private static final long serialVersionUID = -6336711256361320029L;

	private Tabs tabSheet;
	private Tab listTab;
	private Tab songTab;
	private Tab chordsTab;

	private ListTab listTabContent;
	private SongTab songTabContent;
	private ChordsTab chordsTabContent;

	private Div pageLayout;

	public SongsPage() {
		init();
	}

	@Override
	protected void createColumnContent(Div layout) {
		tabSheet = new Tabs();
		layout.add(tabSheet);

		pageLayout = new Div();
		layout.add(pageLayout);

		listTab = new Tab();
		listTab.setLabel("Seznam");
		tabSheet.add(listTab);

		songTab = new Tab();
		songTab.setLabel("Písnička");
		tabSheet.add(songTab);

		chordsTab = new Tab();
		chordsTab.setLabel("Akordy");
		tabSheet.add(chordsTab);

		tabSheet.addSelectedChangeListener(e -> {
			pageLayout.removeAll();
			switch (tabSheet.getSelectedIndex()) {
			default:
			case 0:
				switchListTab();
				break;
			case 1:
				switchSongTab();
				break;
			case 2:
				switchChordsTab();
				break;
			}
		});
		switchListTab();
	}

	private void switchListTab() {
		if (listTabContent == null)
			listTabContent = new ListTab(this);
		pageLayout.removeAll();
		pageLayout.add(listTabContent);
		tabSheet.setSelectedTab(listTab);
	}

	private void switchSongTab() {
		if (songTabContent == null)
			songTabContent = new SongTab(this);
		pageLayout.removeAll();
		pageLayout.add(songTabContent);
		tabSheet.setSelectedTab(songTab);
	}

	private void switchChordsTab() {
		if (chordsTabContent == null)
			chordsTabContent = new ChordsTab(this);
		pageLayout.removeAll();
		pageLayout.add(chordsTabContent);
		tabSheet.setSelectedTab(chordsTab);
	}

}
