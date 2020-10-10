package cz.gattserver.grass3.songs.web;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import cz.gattserver.grass3.ui.pages.template.OneColumnPage;
import cz.gattserver.grass3.ui.util.UIUtils;

@Route("songs")
@PageTitle("Zpěvník")
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

	private Long selectedSongId;
	private String selectedChordId;

	public SongsPage() {
		init();
	}

	@Override
	protected void createColumnContent(Div layout) {
		tabSheet = new Tabs();
		layout.add(tabSheet);

		pageLayout = new Div();
		pageLayout.addClassName(UIUtils.TOP_MARGIN_CSS_CLASS);
		layout.add(pageLayout);

		listTab = new Tab();
		listTab.setLabel("Seznam");
		tabSheet.add(listTab);

		songTab = new Tab();
		songTab.setLabel("Písnička");
		songTab.setEnabled(false);
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

	public void selectListTab() {
		tabSheet.setSelectedTab(listTab);
	}

	public void selectSongTab() {
		tabSheet.setSelectedTab(songTab);
	}

	public void selectChordsTab() {
		tabSheet.setSelectedTab(chordsTab);
	}

	private void switchListTab() {
		pageLayout.removeAll();
		if (listTabContent == null)
			listTabContent = new ListTab(this, selectedSongId);
		listTabContent.selectSong(selectedSongId, false);
		pageLayout.add(listTabContent);
	}

	private void switchSongTab() {
		pageLayout.removeAll();
		if (songTabContent == null)
			songTabContent = new SongTab(this, selectedSongId);
		pageLayout.add(songTabContent);
	}

	private void switchChordsTab() {
		pageLayout.removeAll();
		if (chordsTabContent == null)
			chordsTabContent = new ChordsTab(this, selectedChordId);
		pageLayout.add(chordsTabContent);
	}

	public Long getSelectedSongId() {
		return selectedSongId;
	}

	public void setSelectedSongId(Long selectedSongId) {
		this.selectedSongId = selectedSongId;
		songTab.setEnabled(selectedSongId != null);
	}

	public String getSelectedChordId() {
		return selectedChordId;
	}

	public void setSelectedChordId(String selectedChordId) {
		this.selectedChordId = selectedChordId;
		chordsTab.setEnabled(selectedChordId != null);
	}

}
