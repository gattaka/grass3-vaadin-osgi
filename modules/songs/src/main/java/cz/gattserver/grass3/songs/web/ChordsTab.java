package cz.gattserver.grass3.songs.web;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.server.Page;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.Column;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.components.grid.HeaderRow;
import com.vaadin.ui.themes.ValoTheme;

import cz.gattserver.grass3.security.Role;
import cz.gattserver.grass3.services.SecurityService;
import cz.gattserver.grass3.songs.facades.SongsFacade;
import cz.gattserver.grass3.songs.model.domain.Instrument;
import cz.gattserver.grass3.songs.model.dto.ChordTO;
import cz.gattserver.grass3.ui.components.CreateGridButton;
import cz.gattserver.grass3.ui.components.DeleteGridButton;
import cz.gattserver.grass3.ui.components.ModifyGridButton;
import cz.gattserver.web.common.spring.SpringContextHelper;
import cz.gattserver.web.common.ui.H2Label;

public class ChordsTab extends VerticalLayout {

	private static final long serialVersionUID = 2599065817744507785L;

	@Autowired
	private SongsFacade songsFacade;

	@Autowired
	private SecurityService securityService;

	private Grid<ChordTO> grid;
	private Label nameLabel;
	private VerticalLayout chordDescriptionLayout;

	private ChordTO choosenChord;
	private List<ChordTO> chords;
	private ChordTO filterTO;

	public ChordsTab() {

		SpringContextHelper.inject(this);
		setMargin(new MarginInfo(true, false, false, false));

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

		grid.addSelectionListener((e) -> e.getFirstSelectedItem().ifPresent((v) -> showDetail(v)));

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

		btnLayout.setVisible(securityService.getCurrentUser().getRoles().contains(Role.ADMIN));

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

		btnLayout.addComponent(new DeleteGridButton<ChordTO>("Smazat", items -> {
			for (ChordTO c : items)
				songsFacade.deleteChord(c.getId());
			loadChords();
			showDetail(null);
		}, grid));
	}

	private void showDetail(ChordTO choosenChord) {
		chordDescriptionLayout.removeAllComponents();
		if (choosenChord == null) {
			nameLabel.setValue(null);
			this.choosenChord = null;
		} else {
			nameLabel.setValue(choosenChord.getName());
			Label chordDisplayLabel = new Label();
			switch (choosenChord.getInstrument()) {
			case GUITAR:
				chordDisplayLabel.setContentMode(ContentMode.HTML);
				chordDisplayLabel.setValue(createDisplayForGuitar(choosenChord.getConfiguration()));
				chordDisplayLabel.setStyleName("chord-display-area");
				Page.getCurrent().getStyles().add(
						".v-label.v-widget.chord-display-area.v-label-chord-display-area.v-label-undef-w { font-family: monospace; white-space: pre;}");
			}
			chordDescriptionLayout.addComponent(chordDisplayLabel);
			this.choosenChord = choosenChord;
		}
	}

	private String createDisplayForGuitar(Integer integer) {
		String val = "";
		val += " E   a   d   g   h   e <br/>";
		for (int row = 0; row < 8; row++) {
			if (row % 2 == 0) {
				val += "-----------------------";
			} else
				for (int col = 0; col < 6; col++) {
					int mask = 1 << (row / 2 * 6) + col;
					if ((integer.intValue() & mask) > 0)
						val += " X ";
					else
						val += "   ";
					if (col != 5)
						val += "|";
				}
			val += "<br/>";
		}
		return val;
	}

	private void loadChords() {
		chords.clear();
		chords.addAll(songsFacade.getChords(filterTO));
		grid.getDataProvider().refreshAll();
	}
}
