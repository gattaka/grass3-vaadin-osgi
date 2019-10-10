package cz.gattserver.grass3.songs.web;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.Grid.Column;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.textfield.TextFieldVariant;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MultiFileMemoryBuffer;

import cz.gattserver.grass3.services.SecurityService;
import cz.gattserver.grass3.songs.SongsRole;
import cz.gattserver.grass3.songs.facades.SongsService;
import cz.gattserver.grass3.songs.model.interfaces.SongOverviewTO;
import cz.gattserver.grass3.songs.model.interfaces.SongTO;
import cz.gattserver.grass3.ui.components.button.CreateGridButton;
import cz.gattserver.grass3.ui.components.button.DeleteGridButton;
import cz.gattserver.grass3.ui.components.button.ModifyGridButton;
import cz.gattserver.grass3.ui.util.ButtonLayout;
import cz.gattserver.web.common.spring.SpringContextHelper;

public class ListTab extends Div {

	private static final long serialVersionUID = 594189301140808163L;

	@Autowired
	private SecurityService securityService;

	private transient SongsService songsService;

	@Resource(name = "songsPageFactory")
	private SongsPageFactory pageFactory;

	private Grid<SongOverviewTO> grid;

	private SongTO choosenSong;
	private SongOverviewTO filterTO;

	private SongsPage songsPage;

	private Map<Long, Integer> indexMap = new HashMap<>();

	public ListTab(SongsPage songsPage, Long selectedSongId) {
		SpringContextHelper.inject(this);
		filterTO = new SongOverviewTO();
		this.songsPage = songsPage;

		grid = new Grid<>();
		grid.setMultiSort(false);
		grid.addThemeVariants(GridVariant.LUMO_COLUMN_BORDERS, GridVariant.LUMO_ROW_STRIPES, GridVariant.LUMO_COMPACT);

		Column<SongOverviewTO> nazevColumn = grid.addColumn(SongOverviewTO::getName).setHeader("Název");
		Column<SongOverviewTO> authorColumn = grid.addColumn(SongOverviewTO::getAuthor).setHeader("Autor")
				.setWidth("250px").setFlexGrow(0);
		Column<SongOverviewTO> yearColumn = grid.addColumn(SongOverviewTO::getYear).setHeader("Rok").setWidth("60px")
				.setFlexGrow(0);
		grid.setWidth("100%");
		grid.setHeight("600px");
		add(grid);

		HeaderRow filteringHeader = grid.appendHeaderRow();

		// Název
		TextField nazevColumnField = new TextField();
		nazevColumnField.addThemeVariants(TextFieldVariant.LUMO_SMALL);
		nazevColumnField.setWidth("100%");
		nazevColumnField.addValueChangeListener(e -> {
			filterTO.setName(e.getValue());
			populate();
		});
		filteringHeader.getCell(nazevColumn).setComponent(nazevColumnField);

		// Autor
		TextField authorColumnField = new TextField();
		authorColumnField.addThemeVariants(TextFieldVariant.LUMO_SMALL);
		authorColumnField.setWidth("100%");
		authorColumnField.addValueChangeListener(e -> {
			filterTO.setAuthor(e.getValue());
			populate();
		});
		filteringHeader.getCell(authorColumn).setComponent(authorColumnField);

		// Rok
		TextField yearColumnField = new TextField();
		yearColumnField.addThemeVariants(TextFieldVariant.LUMO_SMALL);
		yearColumnField.setWidth("100%");
		yearColumnField.addValueChangeListener(e -> {
			filterTO.setYear(StringUtils.isBlank(e.getValue()) ? null : Integer.valueOf(e.getValue()));
			populate();
		});
		filteringHeader.getCell(yearColumn).setComponent(yearColumnField);

		populate();

		grid.addItemClickListener((e) -> {
			if (e.getClickCount() > 1)
				selectSong(e.getItem().getId(), true);
		});

		MultiFileMemoryBuffer buffer = new MultiFileMemoryBuffer();

		Upload upload = new Upload(buffer);
		upload.addClassName("top-margin");
		upload.setAcceptedFileTypes("text/plain");
		upload.addSucceededListener(event -> {
			SongTO to = getSongsService().importSong(buffer.getInputStream(event.getFileName()), event.getFileName());
			populate();
		});
		add(upload);

		ButtonLayout btnLayout = new ButtonLayout();
		add(btnLayout);

		btnLayout.setVisible(securityService.getCurrentUser().getRoles().contains(SongsRole.SONGS_EDITOR));

		btnLayout.add(new CreateGridButton("Přidat", event -> {
			new SongDialog() {
				private static final long serialVersionUID = -4863260002363608014L;

				@Override
				protected void onSave(SongTO to) {
					to = getSongsService().saveSong(to);
					populate();
					selectSong(to.getId(), true);
				}
			}.open();
		}));

		btnLayout.add(new ModifyGridButton<SongOverviewTO>("Upravit", event -> {
			new SongDialog(getSongsService().getSongById(grid.getSelectedItems().iterator().next().getId())) {

				private static final long serialVersionUID = 5264621441522056786L;

				@Override
				protected void onSave(SongTO to) {
					to = getSongsService().saveSong(to);
					populate();
					selectSong(to.getId(), false);
				}
			}.open();
		}, grid));

		btnLayout.add(new DeleteGridButton<SongOverviewTO>("Smazat", items -> {
			for (SongOverviewTO s : items)
				getSongsService().deleteSong(s.getId());
			populate();
			songsPage.setSelectedSongId(null);
		}, grid));

		if (selectedSongId != null)
			selectSong(selectedSongId, false);
	}

	public void selectSong(Long id, boolean switchToDetail) {
		SongOverviewTO to = new SongOverviewTO();
		to.setId(id);
		songsPage.setSelectedSongId(id);
		if (switchToDetail) {
			songsPage.switchSongTab();
		} else {
			grid.select(to);
			UI.getCurrent().getPage().executeJs("$0._scrollToIndex(" + indexMap.get(to.getId()) + ")",
					grid.getElement());
		}
	}

	public SongTO getChoosenSong() {
		return choosenSong;
	}

	private SongsService getSongsService() {
		if (songsService == null)
			songsService = SpringContextHelper.getBean(SongsService.class);
		return songsService;
	}

	public void populate() {
		List<SongOverviewTO> songs = getSongsService().getSongs(filterTO);
		indexMap.clear();
		for (int i = 0; i < songs.size(); i++)
			indexMap.put(songs.get(i).getId(), i);
		grid.setItems(songs);
	}

}
