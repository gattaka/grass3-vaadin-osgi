package cz.gattserver.grass3.songs.web;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.server.Page;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.Component;
import com.vaadin.ui.Grid;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

import cz.gattserver.grass3.server.GrassRequest;
import cz.gattserver.grass3.songs.facades.SongsFacade;
import cz.gattserver.grass3.songs.model.dto.SongDTO;
import cz.gattserver.grass3.songs.model.dto.SongOverviewDTO;
import cz.gattserver.grass3.ui.components.CreateGridButton;
import cz.gattserver.grass3.ui.components.DeleteGridButton;
import cz.gattserver.grass3.ui.components.ModifyGridButton;
import cz.gattserver.grass3.ui.pages.template.OneColumnPage;
import cz.gattserver.web.common.ui.H2Label;

public class SongsPage extends OneColumnPage {

	@Autowired
	private SongsFacade songsFacade;

	private Grid<SongOverviewDTO> grid;
	private Label nameLabel;
	private Label authorYearLabel;
	private Label contentLabel;
	private SongDTO choosenSong;
	private List<SongOverviewDTO> songs = new ArrayList<>();

	public SongsPage(GrassRequest request) {
		super(request);
	}

	private void showDetail(SongDTO choosenSong) {
		if (choosenSong == null) {
			nameLabel.setValue(null);
			authorYearLabel.setValue(null);
			contentLabel.setValue(null);
			this.choosenSong = null;
		} else {
			nameLabel.setValue(choosenSong.getName());
			String value = choosenSong.getAuthor();
			if (choosenSong.getYear() != null || choosenSong.getYear().intValue() > 0)
				value = value + " (" + choosenSong.getYear() + ")";
			authorYearLabel.setValue(value);
			String htmlText = "";
			for (String line : choosenSong.getText().split("<br/>")) {
				boolean chordLine = false;
				for (String chunk : line.split(" |,|\t"))
					if (chunk.toLowerCase().matches(".+b|.+#| [aehdcfgb]mi|b|e|h|d|c|f|g")) {
						htmlText += "<span style='color: blue'>" + line + "</span><br/>";
						chordLine = true;
						break;
					}
				if (!chordLine)
					htmlText += line + "<br/>";
			}
			contentLabel.setValue(htmlText);
			this.choosenSong = choosenSong;
		}
	}

	private void loadSongs() {
		songs.clear();
		songs.addAll(songsFacade.getSongs());
		grid.getDataProvider().refreshAll();
	}

	@Override
	protected Component createContent() {
		VerticalLayout layout = new VerticalLayout();
		layout.setSpacing(true);
		layout.setMargin(true);

		HorizontalLayout songsLayout = new HorizontalLayout();
		layout.addComponent(songsLayout);

		grid = new Grid<>(null, songs);
		grid.addColumn(SongOverviewDTO::getName).setCaption("Název");
		grid.addColumn(SongOverviewDTO::getAuthor).setCaption("Autor");
		grid.setWidth("358px");
		grid.setHeight("600px");
		songsLayout.addComponent(grid);

		loadSongs();

		grid.addSelectionListener(
				(e) -> e.getFirstSelectedItem().ifPresent((v) -> showDetail(songsFacade.getSongById(v.getId()))));

		VerticalLayout contentLayout = new VerticalLayout();

		Panel panel = new Panel(contentLayout);
		panel.setWidth("600px");
		panel.setHeight("100%");
		songsLayout.addComponent(panel);
		songsLayout.setExpandRatio(panel, 1);

		nameLabel = new H2Label();
		contentLayout.addComponent(nameLabel);

		authorYearLabel = new Label();
		authorYearLabel.setStyleName("songs-author-year-line");
		Page.getCurrent().getStyles().add(
				".v-label.v-widget.v-label-undef-w.songs-author-year-line.v-label-songs-author-year-line { margin-top: -8px; font-style: italic; }");
		contentLayout.addComponent(authorYearLabel);

		contentLabel = new Label();
		contentLabel.setWidth("560px");
		contentLabel.setContentMode(ContentMode.HTML);
		contentLayout.addComponent(contentLabel);

		HorizontalLayout btnLayout = new HorizontalLayout();
		btnLayout.setSpacing(true);
		layout.addComponent(btnLayout);

		btnLayout.addComponent(new CreateGridButton("Přidat", event -> {
			UI.getCurrent().addWindow(new SongWindow() {
				private static final long serialVersionUID = -4863260002363608014L;

				@Override
				protected void onSave(SongDTO to) {
					to = songsFacade.saveSong(to);
					showDetail(to);
					loadSongs();
				}
			});
		}));

		btnLayout.addComponent(new ModifyGridButton<SongOverviewDTO>("Upravit", event -> {
			UI.getCurrent().addWindow(new SongWindow(choosenSong) {

				private static final long serialVersionUID = 5264621441522056786L;

				@Override
				protected void onSave(SongDTO to) {
					to = songsFacade.saveSong(to);
					showDetail(to);
					loadSongs();
				}
			});
		}, grid));

		btnLayout.addComponent(new DeleteGridButton<SongOverviewDTO>("Smazat", items -> {
			for (SongOverviewDTO s : items)
				songsFacade.deleteSong(s.getId());
			loadSongs();
			showDetail(null);
		}, grid));

		return layout;
	}
}
