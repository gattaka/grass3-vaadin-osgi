package cz.gattserver.grass3.songs.web;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.server.Page;
import com.vaadin.server.ThemeResource;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.Column;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.components.grid.HeaderRow;
import com.vaadin.ui.themes.ValoTheme;

import cz.gattserver.grass3.server.GrassRequest;
import cz.gattserver.grass3.services.SecurityService;
import cz.gattserver.grass3.songs.SongsRole;
import cz.gattserver.grass3.songs.facades.SongsFacade;
import cz.gattserver.grass3.songs.model.domain.Instrument;
import cz.gattserver.grass3.songs.model.interfaces.ChordTO;
import cz.gattserver.grass3.ui.components.CreateGridButton;
import cz.gattserver.grass3.ui.components.DeleteGridButton;
import cz.gattserver.grass3.ui.components.GridButton;
import cz.gattserver.grass3.ui.components.ModifyGridButton;
import cz.gattserver.web.common.spring.SpringContextHelper;
import cz.gattserver.web.common.ui.H2Label;
import cz.gattserver.web.common.ui.ImageIcon;

public class ChordsTab extends VerticalLayout {

	private static final long serialVersionUID = 2599065817744507785L;

	@Autowired
	private SongsFacade songsFacade;

	@Autowired
	private SecurityService securityService;

	@Resource(name = "songsPageFactory")
	private SongsPageFactory pageFactory;

	private GrassRequest request;

	private Grid<ChordTO> grid;
	private Label nameLabel;
	private VerticalLayout chordDescriptionLayout;

	private ChordTO choosenChord;
	private List<ChordTO> chords;
	private ChordTO filterTO;

	public ChordsTab(GrassRequest request) {
		SpringContextHelper.inject(this);
		setMargin(new MarginInfo(true, false, false, false));

		this.request = request;

		chords = new ArrayList<>();
		filterTO = new ChordTO();

		HorizontalLayout mainLayout = new HorizontalLayout();
		addComponent(mainLayout);

		grid = new Grid<>(null, chords);
		Column<ChordTO, String> nazevColumn = grid.addColumn(ChordTO::getName).setCaption("Název");
		Column<ChordTO, String> instrumentColumn = grid.addColumn(c -> c.getInstrument().getCaption())
				.setCaption("Nástroj");
		grid.setWidth("398px");
		grid.setHeight("600px");
		mainLayout.addComponent(grid);
		HeaderRow filteringHeader = grid.appendHeaderRow();

		// Název
		TextField nazevColumnField = new TextField();
		nazevColumnField.addStyleName(ValoTheme.TEXTFIELD_TINY);
		nazevColumnField.addValueChangeListener(e -> {
			filterTO.setName(e.getValue());
			loadChords();
		});
		filteringHeader.getCell(nazevColumn).setComponent(nazevColumnField);

		// Nástroj
		ComboBox<Instrument> instrumentColumnField = new ComboBox<>("Nástroj", Arrays.asList(Instrument.values()));
		instrumentColumnField.setItemCaptionGenerator(Instrument::getCaption);
		instrumentColumnField.addStyleName(ValoTheme.TEXTFIELD_TINY);
		instrumentColumnField.setWidth("100%");
		instrumentColumnField.addValueChangeListener(e -> {
			filterTO.setInstrument(e.getValue());
			loadChords();
		});
		filteringHeader.getCell(instrumentColumn).setComponent(instrumentColumnField);

		loadChords();

		grid.addSelectionListener((e) -> {
			if (e.getFirstSelectedItem().isPresent())
				showDetail(e.getFirstSelectedItem().get());
			else
				showDetail(null);
		});

		VerticalLayout contentLayout = new VerticalLayout();

		Panel panel = new Panel(contentLayout);
		panel.setWidth("560px");
		panel.setHeight("100%");
		mainLayout.addComponent(panel);
		mainLayout.setExpandRatio(panel, 1);

		nameLabel = new H2Label();
		contentLayout.addComponent(nameLabel);

		chordDescriptionLayout = new VerticalLayout();
		contentLayout.addComponent(chordDescriptionLayout);

		HorizontalLayout btnLayout = new HorizontalLayout();
		btnLayout.setSpacing(true);
		addComponent(btnLayout);

		btnLayout.setVisible(securityService.getCurrentUser().getRoles().contains(SongsRole.SONGS_EDITOR));

		btnLayout.addComponent(new CreateGridButton("Přidat", event -> {
			UI.getCurrent().addWindow(new ChordWindow() {
				private static final long serialVersionUID = -4863260002363608014L;

				@Override
				protected void onSave(ChordTO to) {
					to = songsFacade.saveChord(to);
					showDetail(to);
					loadChords();
				}
			});
		}));

		btnLayout.addComponent(new ModifyGridButton<ChordTO>("Upravit", event -> {
			UI.getCurrent().addWindow(new ChordWindow(choosenChord) {

				private static final long serialVersionUID = 5264621441522056786L;

				@Override
				protected void onSave(ChordTO to) {
					to = songsFacade.saveChord(to);
					showDetail(to);
					loadChords();
				}
			});
		}, grid));

		GridButton<ChordTO> copyBtn = new GridButton<>("Kopie", event -> {
			UI.getCurrent().addWindow(new ChordWindow(choosenChord, true) {
				private static final long serialVersionUID = -4863260002363608014L;

				@Override
				protected void onSave(ChordTO to) {
					to = songsFacade.saveChord(to);
					showDetail(to);
					loadChords();
				}
			});
		}, grid);
		copyBtn.setIcon(ImageIcon.QUICKEDIT_16_ICON.createResource());
		btnLayout.addComponent(copyBtn);

		btnLayout.addComponent(new DeleteGridButton<ChordTO>("Smazat", items -> {
			for (ChordTO c : items)
				songsFacade.deleteChord(c.getId());
			loadChords();
			showDetail(null);
		}, grid));
	}

	public void selectChord(String name) {
		ChordTO to = songsFacade.getChordByName(name);
		if (to != null)
			grid.select(to);
	}

	private void showDetail(ChordTO choosenChord) {
		chordDescriptionLayout.removeAllComponents();
		if (choosenChord == null) {
			nameLabel.setValue(null);
			this.choosenChord = null;
			String currentURL = request.getContextRoot() + "/" + pageFactory.getPageName();
			Page.getCurrent().pushState(currentURL);
		} else {
			nameLabel.setValue(choosenChord.getName());
			Label chordDisplayLabel = new Label();
			switch (choosenChord.getInstrument()) {
			case GUITAR:
				createDisplayForGuitar(choosenChord.getConfiguration());
			}
			chordDescriptionLayout.addComponent(chordDisplayLabel);
			this.choosenChord = choosenChord;

			String currentURL;
			try {
				currentURL = request.getContextRoot() + "/" + pageFactory.getPageName() + "/chord/"
						+ URLEncoder.encode(choosenChord.getName(), "UTF-8");
				Page.getCurrent().pushState(currentURL);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
	}

	private void createDisplayForGuitar(Long configuration) {
		GridLayout grid = new GridLayout(7, 9);
		grid.setSpacing(false);
		grid.setMargin(false);
		grid.setDefaultComponentAlignment(Alignment.MIDDLE_LEFT);
		chordDescriptionLayout.addComponent(grid);

		grid.addComponent(new Embedded(null, new ThemeResource("songs/strings_labels.png")), 1, 0, 6, 0);
		for (int row = 1; row < grid.getRows(); row++)
			for (int col = 0; col < grid.getColumns(); col++)
				if (col == 0 && row > 0)
					grid.addComponent(new Label(String.valueOf(row)), col, row);
				else {
					long mask = 1L << (row - 1) * 6 + (col - 1);
					String img = null;
					if ((configuration.longValue() & mask) > 0)
						img = "hit_chord.png";
					else
						img = "empty_chord.png";
					Embedded emb = new Embedded(null, new ThemeResource("songs/" + img));
					emb.setHeight("53px");
					emb.setWidth("34px");
					grid.addComponent(emb, col, row);
				}
	}

	private void loadChords() {
		chords.clear();
		chords.addAll(songsFacade.getChords(filterTO));
		grid.getDataProvider().refreshAll();
	}
}
