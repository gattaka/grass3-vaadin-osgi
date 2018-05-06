package cz.gattserver.grass3.songs.web;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.server.Page;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.Grid;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Grid.Column;
import com.vaadin.ui.components.grid.HeaderRow;
import com.vaadin.ui.themes.ValoTheme;

import cz.gattserver.grass3.security.Role;
import cz.gattserver.grass3.services.SecurityService;
import cz.gattserver.grass3.songs.facades.SongsFacade;
import cz.gattserver.grass3.songs.model.dto.SongOverviewTO;
import cz.gattserver.grass3.songs.model.dto.SongTO;
import cz.gattserver.grass3.ui.components.CreateGridButton;
import cz.gattserver.grass3.ui.components.DeleteGridButton;
import cz.gattserver.grass3.ui.components.ModifyGridButton;
import cz.gattserver.web.common.spring.SpringContextHelper;
import cz.gattserver.web.common.ui.H2Label;
import cz.gattserver.web.common.ui.MultiUpload;

public class TextsTab extends VerticalLayout {

	private static final long serialVersionUID = 594189301140808163L;

	@Autowired
	private SongsFacade songsFacade;

	@Autowired
	private SecurityService securityService;

	private Grid<SongOverviewTO> grid;
	private Label nameLabel;
	private Label authorYearLabel;
	private Label contentLabel;

	private SongTO choosenSong;
	private List<SongOverviewTO> songs;
	private SongOverviewTO filterTO;

	public TextsTab() {
		SpringContextHelper.inject(this);
		setMargin(new MarginInfo(true, false, false, false));

		songs = new ArrayList<>();
		filterTO = new SongOverviewTO();

		HorizontalLayout mainLayout = new HorizontalLayout();
		addComponent(mainLayout);

		grid = new Grid<>(null, songs);
		Column<SongOverviewTO, String> nazevColumn = grid.addColumn(SongOverviewTO::getName).setCaption("Název");
		Column<SongOverviewTO, String> authorColumn = grid.addColumn(SongOverviewTO::getAuthor).setCaption("Autor")
				.setWidth(150);
		Column<SongOverviewTO, Integer> yearColumn = grid.addColumn(SongOverviewTO::getYear).setCaption("Rok");
		grid.setWidth("398px");
		grid.setHeight("600px");
		mainLayout.addComponent(grid);

		HeaderRow filteringHeader = grid.appendHeaderRow();

		// Název
		TextField nazevColumnField = new TextField();
		nazevColumnField.addStyleName(ValoTheme.TEXTFIELD_TINY);
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
		yearColumnField.setWidth("45px");
		yearColumnField.addValueChangeListener(e -> {
			filterTO.setYear(StringUtils.isBlank(e.getValue()) ? null : Integer.valueOf(e.getValue()));
			loadSongs();
		});
		filteringHeader.getCell(yearColumn).setComponent(yearColumnField);

		loadSongs();

		grid.addSelectionListener(
				(e) -> e.getFirstSelectedItem().ifPresent((v) -> showDetail(songsFacade.getSongById(v.getId()))));

		VerticalLayout contentLayout = new VerticalLayout();

		Panel panel = new Panel(contentLayout);
		panel.setWidth("560px");
		panel.setHeight("100%");
		mainLayout.addComponent(panel);
		mainLayout.setExpandRatio(panel, 1);

		nameLabel = new H2Label();
		contentLayout.addComponent(nameLabel);

		authorYearLabel = new Label();
		authorYearLabel.setStyleName("songs-author-year-line");
		Page.getCurrent().getStyles().add(
				".v-label.v-widget.v-label-undef-w.songs-author-year-line.v-label-songs-author-year-line { margin-top: -8px; font-style: italic; }");
		contentLayout.addComponent(authorYearLabel);

		contentLabel = new Label();
		contentLabel.setStyleName("song-text-area");
		Page.getCurrent().getStyles().add(".v-slot.v-slot-song-text-area { font-family: monospace; }");
		contentLabel.setWidth("520px");
		contentLabel.setContentMode(ContentMode.HTML);
		contentLayout.addComponent(contentLabel);

		HorizontalLayout btnLayout = new HorizontalLayout();
		btnLayout.setSpacing(true);
		addComponent(btnLayout);

		btnLayout.setVisible(securityService.getCurrentUser().getRoles().contains(Role.ADMIN));

		btnLayout.addComponent(new CreateGridButton("Přidat", event -> {
			UI.getCurrent().addWindow(new SongWindow() {
				private static final long serialVersionUID = -4863260002363608014L;

				@Override
				protected void onSave(SongTO to) {
					to = songsFacade.saveSong(to);
					showDetail(to);
					loadSongs();
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
				showDetail(to);
				loadSongs();
			}
		};
		multiFileUpload.setEnabled(false);
		btnLayout.addComponent(multiFileUpload);

		importedAuthorField
				.addValueChangeListener(e -> multiFileUpload.setEnabled(StringUtils.isNotBlank(e.getValue())));

		btnLayout.addComponent(new ModifyGridButton<SongOverviewTO>("Upravit", event -> {
			UI.getCurrent().addWindow(new SongWindow(choosenSong) {

				private static final long serialVersionUID = 5264621441522056786L;

				@Override
				protected void onSave(SongTO to) {
					to = songsFacade.saveSong(to);
					showDetail(to);
					loadSongs();
				}
			});
		}, grid));

		btnLayout.addComponent(new DeleteGridButton<SongOverviewTO>("Smazat", items -> {
			for (SongOverviewTO s : items)
				songsFacade.deleteSong(s.getId());
			loadSongs();
			showDetail(null);
		}, grid));
	}

	private void showDetail(SongTO choosenSong) {
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
				boolean chordLine = true;
				for (String chunk : line.split(" +| +|,|\t+"))
					if (StringUtils.isNotBlank(chunk) && !chunk.toLowerCase()
							.matches("(a|b|c|d|e|f|g|h|x|/|#|mi|m|dim|maj|dur|sus|add|[0-9]|-|\\+|\\(|\\)|capo|=|\\.)+")) {
						chordLine = false;
						break;
					}
				htmlText += chordLine ? ("<span style='color: blue; white-space: pre;'>" + line + "</span><br/>")
						: line + "<br/>";
			}
			contentLabel.setValue(htmlText);
			this.choosenSong = choosenSong;
		}
	}

	private void loadSongs() {
		songs.clear();
		songs.addAll(songsFacade.getSongs(filterTO));
		grid.getDataProvider().refreshAll();
	}

}
