package cz.gattserver.grass3.songs.web;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.Resource;
import javax.imageio.ImageIO;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import com.vaadin.server.Page;
import com.vaadin.server.StreamResource;
import com.vaadin.server.StreamResource.StreamSource;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.JavaScript;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

import cz.gattserver.grass3.server.GrassRequest;
import cz.gattserver.grass3.services.SecurityService;
import cz.gattserver.grass3.songs.SongsRole;
import cz.gattserver.grass3.songs.facades.SongsService;
import cz.gattserver.grass3.songs.model.interfaces.ChordTO;
import cz.gattserver.grass3.songs.model.interfaces.SongTO;
import cz.gattserver.grass3.songs.util.ChordImageUtils;
import cz.gattserver.grass3.ui.components.CreateButton;
import cz.gattserver.grass3.ui.components.DeleteButton;
import cz.gattserver.grass3.ui.components.ModifyButton;
import cz.gattserver.web.common.spring.SpringContextHelper;
import cz.gattserver.web.common.ui.H2Label;
import cz.gattserver.web.common.ui.window.WebWindow;

public class SongTab extends VerticalLayout {

	private static final long serialVersionUID = 594189301140808163L;

	private static final Logger logger = LoggerFactory.getLogger(SongTab.class);

	@Autowired
	private SongsService songsFacade;

	@Autowired
	private SecurityService securityService;

	@Resource(name = "songsPageFactory")
	private SongsPageFactory pageFactory;

	private GrassRequest request;
	private TabSheet tabSheet;
	private ChordsTab chordsTab;
	private ListTab listTab;

	private Label nameLabel;
	private Label authorYearLabel;
	private Label contentLabel;

	public SongTab(GrassRequest request, TabSheet tabSheet) {
		SpringContextHelper.inject(this);
		this.request = request;
		this.tabSheet = tabSheet;
	}

	public ChordsTab getChordsTab() {
		return chordsTab;
	}

	public SongTab setChordsTab(ChordsTab chordsTab) {
		this.chordsTab = chordsTab;
		return this;
	}

	public ListTab getListTab() {
		return listTab;
	}

	public SongTab setListTab(ListTab listTab) {
		this.listTab = listTab;
		return this;
	}

	public SongTab init() {
		if (chordsTab == null || listTab == null)
			throw new IllegalStateException();

		setMargin(new MarginInfo(true, false, false, false));
		VerticalLayout wrapLayout = new VerticalLayout();

		Panel panel = new Panel(wrapLayout);
		panel.setWidth("100%");
		panel.setHeight("100%");
		addComponent(panel);

		nameLabel = new H2Label();
		wrapLayout.addComponent(nameLabel);

		authorYearLabel = new Label();
		authorYearLabel.setStyleName("songs-author-year-line");
		Page.getCurrent().getStyles().add(
				".v-label.v-widget.v-label-undef-w.songs-author-year-line.v-label-songs-author-year-line { margin-top: -8px; font-style: italic; }");
		wrapLayout.addComponent(authorYearLabel);

		HorizontalLayout contentLayout = new HorizontalLayout();
		contentLayout.setWidth("100%");
		contentLayout.setHeight("100%");
		wrapLayout.addComponent(contentLayout);

		contentLabel = new Label();
		contentLabel.setStyleName("song-text-area");
		Page.getCurrent().getStyles()
				.add(".v-slot.v-slot-song-text-area { font-family: monospace; font-size: 12px; overflow: auto; }");
		Page.getCurrent().getStyles()
				.add(".song-text-area { -webkit-column-width: 300px; -moz-column-width: 300px; column-width: 300px; column-fill: auto; "
						+ "-webkit-column-rule: 1px dotted #ddd; -moz-column-rule: 1px dotted #ddd; column-rule: 1px dotted #ddd; }");
		contentLabel.setHeight("700px");
		contentLabel.setWidth(null);
		contentLabel.setContentMode(ContentMode.HTML);
		contentLayout.addComponent(contentLabel);

		HorizontalLayout btnLayout = new HorizontalLayout();
		btnLayout.setSpacing(true);
		addComponent(btnLayout);

		btnLayout.setVisible(securityService.getCurrentUser().getRoles().contains(SongsRole.SONGS_EDITOR));

		btnLayout.addComponent(new CreateButton("Přidat", event -> {
			UI.getCurrent().addWindow(new SongWindow() {
				private static final long serialVersionUID = -4863260002363608014L;

				@Override
				protected void onSave(SongTO to) {
					to = songsFacade.saveSong(to);
					listTab.populate();
					listTab.chooseSong(to.getId(), false);
				}
			});
		}));

		btnLayout.addComponent(new ModifyButton("Upravit", event -> {
			UI.getCurrent().addWindow(new SongWindow(listTab.getChoosenSong()) {
				private static final long serialVersionUID = 5264621441522056786L;

				@Override
				protected void onSave(SongTO to) {
					to = songsFacade.saveSong(to);
					listTab.populate();
					listTab.chooseSong(to.getId(), false);
				}
			});
		}));

		btnLayout.addComponent(new DeleteButton("Smazat", e -> {
			songsFacade.deleteSong(listTab.getChoosenSong().getId());
			listTab.populate();
			showDetail(null);
			tabSheet.setSelectedTab(listTab);
		}));

		return this;
	}

	public void showDetail(SongTO choosenSong) {
		if (choosenSong == null) {
			nameLabel.setValue(null);
			authorYearLabel.setValue(null);
			contentLabel.setValue(null);
			String currentURL = request.getContextRoot() + "/" + pageFactory.getPageName();
			Page.getCurrent().pushState(currentURL);
		} else {
			nameLabel.setValue(choosenSong.getName());
			String value = choosenSong.getAuthor();
			if (choosenSong.getYear() != null && choosenSong.getYear().intValue() > 0)
				value = value + " (" + choosenSong.getYear() + ")";
			authorYearLabel.setValue(value);
			Set<String> chords = songsFacade.getChords(new ChordTO()).stream().map(ChordTO::getName)
					.collect(Collectors.toSet());
			String htmlText = "";
			for (String line : choosenSong.getText().split("<br/>")) {
				boolean chordLine = true;
				for (String chunk : line.split(" +| +|,|\t+"))
					if (StringUtils.isNotBlank(chunk) && !chunk.toLowerCase().matches(
							"(a|b|c|d|e|f|g|h|x|/|#|mi|m|dim|maj|dur|sus|add|[0-9]|-|\\+|\\(|\\)|capo|=|\\.)+")) {
						chordLine = false;
						break;
					}
				for (String c : chords) {
					String chordLink = c;
					try {
						chordLink = "<a target='_blank' href='" + request.getContextRoot() + "/"
								+ pageFactory.getPageName() + "/chord/" + URLEncoder.encode(c, "UTF-8") + "'"
								+ "onmouseover='grass.chords.show(\"" + c + "\", event.clientX, event.clientY)' "
								+ "onmouseout='grass.chords.hide()' " + ">" + c + "</a>";
					} catch (UnsupportedEncodingException e) {
						logger.error("Chord link se nezdařilo vytvořit", e);
					}
					line = line.replaceAll(c + " ", chordLink + " ");
					line = line.replaceAll(c + ",", chordLink + ",");
					line = line.replaceAll(c + "\\)", chordLink + ")");
					line = line.replaceAll(c + "\\(", chordLink + "(");
					line = line.replaceAll(c + "$", chordLink);
				}
				htmlText += chordLine ? ("<span style='color: blue; white-space: pre;'>" + line + "</span><br/>")
						: ("<span style='white-space: pre;'>" + line + "</span><br/>");
			}
			contentLabel.setValue(htmlText);

			String currentURL;
			try {
				currentURL = request.getContextRoot() + "/" + pageFactory.getPageName() + "/text/" + choosenSong.getId()
						+ "-" + URLEncoder.encode(choosenSong.getName(), "UTF-8");
				Page.getCurrent().pushState(currentURL);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}

		JavaScript.getCurrent().addFunction("grass.chords.show",
				args -> showHoverChord(args.getString(0), args.getNumber(1), args.getNumber(2)));
		JavaScript.getCurrent().addFunction("grass.chords.hide", args -> hideAllWindows());
	}

	private void hideAllWindows() {
		UI ui = UI.getCurrent();
		Collection<Window> windows = Collections.unmodifiableCollection(ui.getWindows());
		for (Window w : windows)
			ui.removeWindow(w);
	}

	private void showHoverChord(String chord, double clientX, double clientY) {
		hideAllWindows();

		ChordTO to = songsFacade.getChordByName(chord);
		BufferedImage image = ChordImageUtils.drawChord(to, 20);

		Window window = new WebWindow(chord);
		VerticalLayout layout = new VerticalLayout();
		layout.addComponent(new Image(null, new StreamResource(new StreamSource() {
			private static final long serialVersionUID = -5893071133311094692L;

			@Override
			public InputStream getStream() {
				ByteArrayOutputStream os = new ByteArrayOutputStream();
				try {
					ImageIO.write(image, "png", os);
					return new ByteArrayInputStream(os.toByteArray());
				} catch (IOException e) {
					logger.error("Nezdařilo se vytváření thumbnail akordu", e);
					return null;
				}
			}
		}, "Chord-" + chord)));
		window.setContent(layout);
		window.setModal(false);
		window.setPositionX((int) clientX + 5);
		window.setPositionY((int) clientY + 5);
		UI.getCurrent().addWindow(window);
	}

}
