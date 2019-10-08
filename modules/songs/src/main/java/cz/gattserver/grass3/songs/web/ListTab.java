package cz.gattserver.grass3.songs.web;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.grid.Grid.Column;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.textfield.TextFieldVariant;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MultiFileMemoryBuffer;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.provider.CallbackDataProvider.CountCallback;
import com.vaadin.flow.data.provider.CallbackDataProvider.FetchCallback;

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
			if (e.getClickCount() > 1) {
				selectSong(e.getItem().getId());
				openSongPage();
			}
		});

		MultiFileMemoryBuffer buffer = new MultiFileMemoryBuffer();

		Upload upload = new Upload(buffer);
		upload.addClassName("top-margin");
		upload.setAcceptedFileTypes("text/plain");
		upload.addSucceededListener(event -> {
			SongTO to = getSongsService().importSong(buffer.getInputStream(event.getFileName()), event.getFileName());
			populate();
			selectSong(to.getId());
			openSongPage();
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
					selectSong(to.getId());
					openSongPage();
				}
			}.open();
		}));

		btnLayout.add(new ModifyGridButton<SongOverviewTO>("Upravit", event -> {
			new SongDialog(getSongsService().getSongById(grid.getSelectedItems().iterator().next().getId())) {

				private static final long serialVersionUID = 5264621441522056786L;

				@Override
				protected void onSave(SongTO to) {
					to = getSongsService().saveSong(to);
					// TODO
					// songsPage.showDetail(to);
					populate();
					selectSong(to.getId());
				}
			}.open();
		}, grid));

		btnLayout.add(new DeleteGridButton<SongOverviewTO>("Smazat", items -> {
			for (SongOverviewTO s : items)
				getSongsService().deleteSong(s.getId());
			populate();
			// TODO
			// songsPage.showDetail(null);
		}, grid));

		if (selectedSongId != null)
			selectSong(selectedSongId);
	}

	public void selectSong(Long id) {
		SongOverviewTO to = new SongOverviewTO();
		to.setId(id);
		grid.select(to);
		songsPage.setSelectedSongId(id);
	}

	public void openSongPage() {
		songsPage.switchSongTab();
		// TODO
		// String currentURL;
		// try {
		// currentURL = request.getContextRoot() + "/" +
		// pageFactory.getPageName() + "/text/" + choosenSong.getId()
		// + "-" + URLEncoder.encode(choosenSong.getName(), "UTF-8");
		// Page.getCurrent().open(currentURL, "_blank");
		// } catch (UnsupportedEncodingException e) {
		// e.printStackTrace();
		// }
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
		FetchCallback<SongOverviewTO, Void> fetchCallback = q -> getSongsService()
				.getSongs(filterTO, q.getOffset(), q.getLimit()).stream();
		CountCallback<SongOverviewTO, Void> countCallback = q -> getSongsService().getSongsCount(filterTO);
		grid.setDataProvider(DataProvider.fromCallbacks(fetchCallback, countCallback));
	}

}
