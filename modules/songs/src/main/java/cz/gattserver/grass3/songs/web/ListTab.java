package cz.gattserver.grass3.songs.web;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.shared.ui.grid.ScrollDestination;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.Column;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.components.grid.HeaderRow;
import com.vaadin.ui.themes.ValoTheme;

import cz.gattserver.grass3.server.GrassRequest;
import cz.gattserver.grass3.services.SecurityService;
import cz.gattserver.grass3.songs.SongsRole;
import cz.gattserver.grass3.songs.facades.SongsFacade;
import cz.gattserver.grass3.songs.model.interfaces.SongOverviewTO;
import cz.gattserver.grass3.songs.model.interfaces.SongTO;
import cz.gattserver.grass3.ui.components.CreateGridButton;
import cz.gattserver.grass3.ui.components.DeleteGridButton;
import cz.gattserver.grass3.ui.components.ModifyGridButton;
import cz.gattserver.web.common.spring.SpringContextHelper;
import cz.gattserver.web.common.ui.MultiUpload;

public class ListTab extends VerticalLayout {

	private static final long serialVersionUID = 594189301140808163L;

	@Autowired
	private SongsFacade songsFacade;

	@Autowired
	private SecurityService securityService;

	@Resource(name = "songsPageFactory")
	private SongsPageFactory pageFactory;

	private Grid<SongOverviewTO> grid;

	private SongTO choosenSong;
	private List<SongOverviewTO> songs;
	private SongOverviewTO filterTO;

	private TabSheet tabSheet;
	private SongTab songTab;

	public ListTab(GrassRequest request, TabSheet tabSheet, SongTab songTab) {
		SpringContextHelper.inject(this);
		setMargin(new MarginInfo(true, false, false, false));

		songs = new ArrayList<>();
		filterTO = new SongOverviewTO();
		this.tabSheet = tabSheet;
		this.songTab = songTab;

		grid = new Grid<>(null, songs);
		Column<SongOverviewTO, String> nazevColumn = grid.addColumn(SongOverviewTO::getName).setCaption("Název");
		Column<SongOverviewTO, String> authorColumn = grid.addColumn(SongOverviewTO::getAuthor).setCaption("Autor")
				.setWidth(250);
		Column<SongOverviewTO, Integer> yearColumn = grid.addColumn(SongOverviewTO::getYear).setCaption("Rok")
				.setWidth(60);
		grid.setWidth("100%");
		grid.setHeight("600px");
		addComponent(grid);

		HeaderRow filteringHeader = grid.appendHeaderRow();

		// Název
		TextField nazevColumnField = new TextField();
		nazevColumnField.addStyleName(ValoTheme.TEXTFIELD_TINY);
		nazevColumnField.setWidth("100%");
		nazevColumnField.addValueChangeListener(e -> {
			filterTO.setName(e.getValue());
			loadSongs();
		});
		filteringHeader.getCell(nazevColumn).setComponent(nazevColumnField);

		// Autor
		TextField authorColumnField = new TextField();
		authorColumnField.addStyleName(ValoTheme.TEXTFIELD_TINY);
		authorColumnField.setWidth("100%");
		authorColumnField.addValueChangeListener(e -> {
			filterTO.setAuthor(e.getValue());
			loadSongs();
		});
		filteringHeader.getCell(authorColumn).setComponent(authorColumnField);

		// Rok
		TextField yearColumnField = new TextField();
		yearColumnField.addStyleName(ValoTheme.TEXTFIELD_TINY);
		yearColumnField.setWidth("100%");
		yearColumnField.addValueChangeListener(e -> {
			filterTO.setYear(StringUtils.isBlank(e.getValue()) ? null : Integer.valueOf(e.getValue()));
			loadSongs();
		});
		filteringHeader.getCell(yearColumn).setComponent(yearColumnField);

		loadSongs();

		grid.addItemClickListener((e) -> {
			if (e.getMouseEventDetails().isDoubleClick())
				chooseSong(e.getItem().getId(), false);
		});

		HorizontalLayout btnLayout = new HorizontalLayout();
		btnLayout.setSpacing(true);
		addComponent(btnLayout);

		btnLayout.setVisible(securityService.getCurrentUser().getRoles().contains(SongsRole.SONGS_EDITOR));

		btnLayout.addComponent(new CreateGridButton("Přidat", event -> {
			UI.getCurrent().addWindow(new SongWindow() {
				private static final long serialVersionUID = -4863260002363608014L;

				@Override
				protected void onSave(SongTO to) {
					to = songsFacade.saveSong(to);
					songTab.showDetail(to);
					loadSongs();
					selectSong(to.getId());
				}
			});
		}));

		final TextField importedAuthorField = new TextField();
		importedAuthorField.setPlaceholder("Autor importovaných písní");
		importedAuthorField.setWidth("200px");
		btnLayout.addComponent(importedAuthorField);

		MultiUpload multiFileUpload = new MultiUpload("Import") {
			private static final long serialVersionUID = -415832652157894459L;

			public void fileUploadFinished(InputStream in, String fileName, String mime, long size,
					int filesLeftInQueue) {
				SongTO to = songsFacade.importSong(importedAuthorField.getValue(), in, fileName, mime, size,
						filesLeftInQueue);
				songTab.showDetail(to);
				loadSongs();
				selectSong(to.getId());
			}
		};
		multiFileUpload.setEnabled(false);
		btnLayout.addComponent(multiFileUpload);

		importedAuthorField
				.addValueChangeListener(e -> multiFileUpload.setEnabled(StringUtils.isNotBlank(e.getValue())));

		btnLayout.addComponent(new ModifyGridButton<SongOverviewTO>("Upravit", event -> {
			UI.getCurrent().addWindow(
					new SongWindow(songsFacade.getSongById(grid.getSelectedItems().iterator().next().getId())) {

						private static final long serialVersionUID = 5264621441522056786L;

						@Override
						protected void onSave(SongTO to) {
							to = songsFacade.saveSong(to);
							songTab.showDetail(to);
							loadSongs();
							selectSong(to.getId());
						}
					});
		}, grid));

		btnLayout.addComponent(new DeleteGridButton<SongOverviewTO>("Smazat", items -> {
			for (SongOverviewTO s : items)
				songsFacade.deleteSong(s.getId());
			loadSongs();
			songTab.showDetail(null);
		}, grid));

	}

	public void selectSong(Long id) {
		int row = 0;
		for (SongOverviewTO to : songs) {
			if (to.getId().equals(id)) {
				grid.select(to);
				grid.scrollTo(row, ScrollDestination.MIDDLE);
				return;
			}
			row++;
		}
	}

	public void chooseSong(Long id, boolean selectSong) {
		choosenSong = songsFacade.getSongById(id);
		songTab.showDetail(choosenSong);
		tabSheet.setSelectedTab(songTab);
		if (selectSong)
			selectSong(id);
	}

	public SongTO getChoosenSong() {
		return choosenSong;
	}

	private void loadSongs() {
		songs.clear();
		songs.addAll(songsFacade.getSongs(filterTO));
		grid.getDataProvider().refreshAll();
	}

}
