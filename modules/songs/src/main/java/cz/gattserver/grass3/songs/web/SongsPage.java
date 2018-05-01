package cz.gattserver.grass3.songs.web;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

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
import cz.gattserver.grass3.ui.components.ModifyGridButton;
import cz.gattserver.grass3.ui.pages.template.OneColumnPage;
import cz.gattserver.web.common.ui.H2Label;

public class SongsPage extends OneColumnPage {

	@Autowired
	private SongsFacade songsFacade;

	private Label nameLabel;
	private Label authorLabel;
	private Label yearLabel;
	private Label contentLabel;
	private SongDTO choosenSong;
	private List<SongOverviewDTO> songs;

	public SongsPage(GrassRequest request) {
		super(request);
	}

	private void showDetail(SongDTO choosenSong) {
		nameLabel.setValue(choosenSong.getName());
		authorLabel.setValue(choosenSong.getAuthor());
		yearLabel.setValue(String.valueOf(choosenSong.getYear()));
		contentLabel.setValue(songsFacade.eolToBreakline(choosenSong.getDescription()));
		this.choosenSong = choosenSong;
	}

	private void loadSongs() {
		songs = songsFacade.getSongs();
	}

	@Override
	protected Component createContent() {
		VerticalLayout layout = new VerticalLayout();
		layout.setSpacing(true);
		layout.setMargin(true);

		HorizontalLayout songsLayout = new HorizontalLayout();
		layout.addComponent(songsLayout);

		loadSongs();
		Grid<SongOverviewDTO> grid = new Grid<>(null, songs);
		grid.addColumn(SongOverviewDTO::getName).setCaption("Název");
		grid.addColumn(SongOverviewDTO::getAuthor).setCaption("Autor");
		grid.setWidth("358px");
		grid.setHeight("600px");
		songsLayout.addComponent(grid);

		grid.addSelectionListener(
				(e) -> e.getFirstSelectedItem().ifPresent((v) -> showDetail(songsFacade.getSongById(v.getId()))));

		VerticalLayout contentLayout = new VerticalLayout();
		// contentLayout.setWidth("100%");
		Panel panel = new Panel(contentLayout);
		panel.setWidth("600px");
		panel.setHeight("100%");
		songsLayout.addComponent(panel);
		songsLayout.setExpandRatio(panel, 1);

		nameLabel = new H2Label();
		contentLayout.addComponent(nameLabel);

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
					long id = songsFacade.saveSong(to);
					to.setId(id);
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
					songsFacade.saveSong(to);
					showDetail(to);
					loadSongs();
				}
			});
		}, grid));

		return layout;
	}
}
