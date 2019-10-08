package cz.gattserver.grass3.songs.web;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Resource;
import javax.imageio.ImageIO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.Grid.Column;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.textfield.TextFieldVariant;
import com.vaadin.flow.server.StreamResource;

import cz.gattserver.grass3.services.SecurityService;
import cz.gattserver.grass3.songs.SongsRole;
import cz.gattserver.grass3.songs.facades.SongsService;
import cz.gattserver.grass3.songs.model.domain.Instrument;
import cz.gattserver.grass3.songs.model.interfaces.ChordTO;
import cz.gattserver.grass3.songs.util.ChordImageUtils;
import cz.gattserver.grass3.ui.components.button.CreateGridButton;
import cz.gattserver.grass3.ui.components.button.DeleteGridButton;
import cz.gattserver.grass3.ui.components.button.GridButton;
import cz.gattserver.grass3.ui.components.button.ModifyGridButton;
import cz.gattserver.grass3.ui.pages.template.GrassPage;
import cz.gattserver.web.common.spring.SpringContextHelper;
import cz.gattserver.web.common.ui.ImageIcon;

public class ChordsTab extends Div {

	private static final long serialVersionUID = 2599065817744507785L;

	private static final Logger logger = LoggerFactory.getLogger(ChordsTab.class);

	@Autowired
	private SongsService songsFacade;

	@Autowired
	private SecurityService securityService;

	@Resource(name = "songsPageFactory")
	private SongsPageFactory pageFactory;

	private Grid<ChordTO> grid;
	private H2 nameLabel;
	private VerticalLayout chordDescriptionLayout;

	private ChordTO choosenChord;
	private List<ChordTO> chords;
	private ChordTO filterTO;

	private SongsPage songsPage;

	public ChordsTab(SongsPage songsPage, String chordId) {
		SpringContextHelper.inject(this);

		// GrassPage.getContextPath() + "/" + pageFactory.getPageName() + "/chord/" + URLEncoder.encode(c, "UTF-8")
		
		this.songsPage = songsPage;

		chords = new ArrayList<>();
		filterTO = new ChordTO();

		HorizontalLayout mainLayout = new HorizontalLayout();
		add(mainLayout);

		grid = new Grid<>();
		grid.setItems(chords);
		Column<ChordTO> nazevColumn = grid.addColumn(ChordTO::getName).setHeader("Název");
		Column<ChordTO> instrumentColumn = grid.addColumn(c -> c.getInstrument().getCaption()).setHeader("Nástroj");
		grid.setWidth("398px");
		grid.setHeight("600px");
		mainLayout.add(grid);
		HeaderRow filteringHeader = grid.appendHeaderRow();

		// Název
		TextField nazevColumnField = new TextField();
		nazevColumnField.addThemeVariants(TextFieldVariant.LUMO_SMALL);
		nazevColumnField.addValueChangeListener(e -> {
			filterTO.setName(e.getValue());
			loadChords();
		});
		filteringHeader.getCell(nazevColumn).setComponent(nazevColumnField);

		// Nástroj
		ComboBox<Instrument> instrumentColumnField = new ComboBox<>("Nástroj", Arrays.asList(Instrument.values()));
		instrumentColumnField.getElement().setAttribute("theme", TextFieldVariant.LUMO_SMALL.getVariantName());
		instrumentColumnField.setItemLabelGenerator(Instrument::getCaption);
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

		Div panel = new Div(contentLayout);
		panel.setWidth("560px");
		panel.setHeight("100%");
		mainLayout.add(panel);

		nameLabel = new H2();
		contentLayout.add(nameLabel);

		chordDescriptionLayout = new VerticalLayout();
		contentLayout.add(chordDescriptionLayout);

		HorizontalLayout btnLayout = new HorizontalLayout();
		btnLayout.setSpacing(true);
		add(btnLayout);

		btnLayout.setVisible(securityService.getCurrentUser().getRoles().contains(SongsRole.SONGS_EDITOR));

		btnLayout.add(new CreateGridButton("Přidat", event -> {
			new ChordDialog() {
				private static final long serialVersionUID = -4863260002363608014L;

				@Override
				protected void onSave(ChordTO to) {
					to = songsFacade.saveChord(to);
					showDetail(to);
					loadChords();
				}
			}.open();
		}));

		btnLayout.add(new ModifyGridButton<ChordTO>("Upravit", event -> {
			new ChordDialog(choosenChord) {

				private static final long serialVersionUID = 5264621441522056786L;

				@Override
				protected void onSave(ChordTO to) {
					to = songsFacade.saveChord(to);
					showDetail(to);
					loadChords();
				}
			}.open();
		}, grid));

		GridButton<ChordTO> copyBtn = new GridButton<>("Kopie", event -> {
			new ChordDialog(choosenChord, true) {
				private static final long serialVersionUID = -4863260002363608014L;

				@Override
				protected void onSave(ChordTO to) {
					to = songsFacade.saveChord(to);
					showDetail(to);
					loadChords();
				}
			}.open();
		}, grid);
		copyBtn.setIcon(new Image(ImageIcon.QUICKEDIT_16_ICON.createResource(), "Kopie"));
		btnLayout.add(copyBtn);

		btnLayout.add(new DeleteGridButton<ChordTO>("Smazat", items -> {
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
		chordDescriptionLayout.removeAll();
		if (choosenChord == null) {
			nameLabel.setVisible(false);
			this.choosenChord = null;
			// TODO
			// String currentURL = request.getContextRoot() + "/" +
			// pageFactory.getPageName();
			// Page.getCurrent().pushState(currentURL);
		} else {
			nameLabel.setText(choosenChord.getName());
			nameLabel.setVisible(true);
			Span chordDisplayLabel = new Span();
			switch (choosenChord.getInstrument()) {
			case GUITAR:
				createDisplayForGuitar(choosenChord);
			}
			chordDescriptionLayout.add(chordDisplayLabel);
			this.choosenChord = choosenChord;

			// TODO
			// String currentURL;
			// try {
			// currentURL = request.getContextRoot() + "/" +
			// pageFactory.getPageName() + "/chord/"
			// + URLEncoder.encode(choosenChord.getName(), "UTF-8");
			// Page.getCurrent().pushState(currentURL);
			// } catch (UnsupportedEncodingException e) {
			// e.printStackTrace();
			// }
		}
	}

	private void createDisplayForGuitar(ChordTO choosenChord) {
		BufferedImage image = ChordImageUtils.drawChord(choosenChord, 30);
		VerticalLayout layout = new VerticalLayout();
		chordDescriptionLayout.add(layout);
		String name = "Chord-" + choosenChord.getName();
		layout.add(new Image(new StreamResource(name, () -> {
			ByteArrayOutputStream os = new ByteArrayOutputStream();
			try {
				ImageIO.write(image, "png", os);
				return new ByteArrayInputStream(os.toByteArray());
			} catch (IOException e) {
				logger.error("Nezdařilo se vytváření thumbnail akordu", e);
				return null;
			}
		}), name));
	}

	private void loadChords() {
		chords.clear();
		chords.addAll(songsFacade.getChords(filterTO));
		grid.getDataProvider().refreshAll();
	}
}
