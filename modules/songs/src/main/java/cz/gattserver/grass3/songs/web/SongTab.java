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

import com.vaadin.flow.component.ClientCallable;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.server.StreamResource;

import cz.gattserver.grass3.services.SecurityService;
import cz.gattserver.grass3.songs.SongsRole;
import cz.gattserver.grass3.songs.facades.SongsService;
import cz.gattserver.grass3.songs.model.interfaces.ChordTO;
import cz.gattserver.grass3.songs.model.interfaces.SongTO;
import cz.gattserver.grass3.songs.util.ChordImageUtils;
import cz.gattserver.grass3.ui.components.button.CreateButton;
import cz.gattserver.grass3.ui.components.button.DeleteButton;
import cz.gattserver.grass3.ui.components.button.ModifyButton;
import cz.gattserver.grass3.ui.pages.template.GrassPage;
import cz.gattserver.grass3.ui.util.UIUtils;
import cz.gattserver.web.common.server.URLIdentifierUtils;
import cz.gattserver.web.common.spring.SpringContextHelper;
import cz.gattserver.web.common.ui.HtmlDiv;
import cz.gattserver.web.common.ui.window.WebDialog;

public class SongTab extends VerticalLayout {

	private static final long serialVersionUID = 594189301140808163L;

	private static final Logger logger = LoggerFactory.getLogger(SongTab.class);

	private static final String HOVER_DIV_ID = "chord-detail-hover-div";

	@Autowired
	private SongsService songsFacade;

	@Autowired
	private SecurityService securityService;

	@Resource(name = "songsPageFactory")
	private SongsPageFactory pageFactory;

	private ChordsTab chordsTab;
	private ListTab listTab;

	private H2 nameLabel;
	private HtmlDiv authorYearLabel;
	private HtmlDiv contentLabel;

	private SongsPage songsPage;

	public SongTab(SongsPage songsPage) {
		SpringContextHelper.inject(this);
		this.songsPage = songsPage;
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

		nameLabel = new H2();
		add(nameLabel);

		authorYearLabel = new HtmlDiv();
		authorYearLabel.getStyle().set("margin-top", " -8px").set("font-style", "italic");
		add(authorYearLabel);

		HorizontalLayout contentLayout = new HorizontalLayout();
		contentLayout.setWidth("100%");
		contentLayout.setHeight("100%");
		add(contentLayout);

		contentLabel = new HtmlDiv();
		contentLabel.getStyle().set("font-family", "monospace").set("font-size", "12px").set("overflow", "auto")
				.set("-webkit-column-width", "300px").set("-moz-column-width", "300px").set("column-width", "300px")
				.set("column-fill", "auto").set("-webkit-column-rule", "1px dotted #ddd")
				.set("-moz-column-rule", "1px dotted #ddd").set("column-rule", "1px dotted #ddd");
		contentLabel.setHeight("700px");
		contentLabel.setWidth(null);
		contentLayout.add(contentLabel);

		HorizontalLayout btnLayout = new HorizontalLayout();
		btnLayout.setSpacing(true);
		add(btnLayout);

		btnLayout.setVisible(securityService.getCurrentUser().getRoles().contains(SongsRole.SONGS_EDITOR));

		btnLayout.add(new CreateButton("Přidat", event -> {
			new SongDialog() {
				private static final long serialVersionUID = -4863260002363608014L;

				@Override
				protected void onSave(SongTO to) {
					to = songsFacade.saveSong(to);
					listTab.populate();
					listTab.chooseSong(to.getId(), false);
				}
			}.open();
		}));

		btnLayout.add(new ModifyButton("Upravit", event -> {
			new SongDialog(listTab.getChoosenSong()) {
				private static final long serialVersionUID = 5264621441522056786L;

				@Override
				protected void onSave(SongTO to) {
					to = songsFacade.saveSong(to);
					listTab.populate();
					listTab.chooseSong(to.getId(), false);
				}
			}.open();
		}));

		btnLayout.add(new DeleteButton("Smazat", e -> {
			songsFacade.deleteSong(listTab.getChoosenSong().getId());
			listTab.populate();
			showDetail(null);
			// TODO
			// tabSheet.setSelectedTab(listTab);
		}));

		Div chordDiv = new Div();
		chordDiv.setVisible(false);
		chordDiv.getStyle().set("position", "absolute");
		add(chordDiv);

		Div hoverDiv = new Div() {
			private static final long serialVersionUID = -7319482130016598549L;

			@ClientCallable
			private void chordCallback(String action, String chord, double x, double y) {
				chordDiv.setVisible(true);
				chordDiv.removeAll();
				ChordTO to = songsFacade.getChordByName(chord);
				BufferedImage image = ChordImageUtils.drawChord(to, 20);
				String name = "Chord-" + chord;
				chordDiv.add(new Image(new StreamResource(name, () -> {
					ByteArrayOutputStream os = new ByteArrayOutputStream();
					try {
						ImageIO.write(image, "png", os);
						return new ByteArrayInputStream(os.toByteArray());
					} catch (IOException e) {
						logger.error("Nezdařilo se vytváření thumbnail akordu", e);
						return null;
					}
				}), name));
				chordDiv.getStyle().set("left", x + "px").set("top", y + "px");
			}

			@ClientCallable
			private void hideCallback() {
				chordDiv.setVisible(false);
			}
		};
		hoverDiv.setId(HOVER_DIV_ID);
		add(hoverDiv);

		return this;
	}

	public void showDetail(SongTO choosenSong) {
		if (choosenSong == null) {
			nameLabel.setText(null);
			authorYearLabel.setValue(null);
			contentLabel.setValue(null);
			// TODO
			// String currentURL = request.getContextRoot() + "/" +
			// pageFactory.getPageName();
			// Page.getCurrent().pushState(currentURL);
		} else {
			nameLabel.setText(choosenSong.getName());
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
						chordLink = "<a target='_blank' href='" + GrassPage.getContextPath() + "/"
								+ pageFactory.getPageName() + "/chord/" + URLEncoder.encode(c, "UTF-8") + "'"
								+ "onmouseover='document.getElementById(\"" + HOVER_DIV_ID
								+ "\").$server.chordCallback(\"" + c + "\", event.clientX, event.clientY)' "
								+ "onmouseout='document.getElementById(\"" + HOVER_DIV_ID
								+ "\").$server.hideCallback()' >" + c + "</a>";
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

			// TODO
			// String currentURL;
			// try {
			// currentURL = request.getContextRoot() + "/" +
			// pageFactory.getPageName() + "/text/" + choosenSong.getId()
			// + "-" + URLEncoder.encode(choosenSong.getName(), "UTF-8");
			// Page.getCurrent().pushState(currentURL);
			// } catch (UnsupportedEncodingException e) {
			// e.printStackTrace();
			// }
		}

	}

}
