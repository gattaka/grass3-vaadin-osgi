package cz.gattserver.grass3.songs.web;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.server.Page;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.JavaScript;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.VerticalLayout;

import cz.gattserver.grass3.server.GrassRequest;
import cz.gattserver.grass3.songs.facades.SongsFacade;
import cz.gattserver.grass3.songs.model.interfaces.ChordTO;
import cz.gattserver.grass3.songs.model.interfaces.SongTO;
import cz.gattserver.web.common.spring.SpringContextHelper;
import cz.gattserver.web.common.ui.H2Label;

public class SongTab extends VerticalLayout {

	private static final long serialVersionUID = 594189301140808163L;

	@Autowired
	private SongsFacade songsFacade;

	@Resource(name = "songsPageFactory")
	private SongsPageFactory pageFactory;

	private GrassRequest request;
	private TabSheet tabSheet;
	private ChordsTab chordsTab;

	private Label nameLabel;
	private Label authorYearLabel;
	private Label contentLabel;

	public SongTab(GrassRequest request, TabSheet tabSheet, ChordsTab chordsTab) {
		SpringContextHelper.inject(this);
		setMargin(new MarginInfo(true, false, false, false));

		this.request = request;
		this.tabSheet = tabSheet;
		this.chordsTab = chordsTab;

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
		Page.getCurrent().getStyles().add(
				".song-text-area { -webkit-column-width: 300px; -moz-column-width: 300px; column-width: 300px; column-fill: auto; "
				+ "-webkit-column-rule: 1px dotted #ddd; -moz-column-rule: 1px dotted #ddd; column-rule: 1px dotted #ddd; }");
		contentLabel.setHeight("700px");
		contentLabel.setWidth(null);
		contentLabel.setContentMode(ContentMode.HTML);
		contentLayout.addComponent(contentLabel);

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
					// String chordLink = "<a target='_blank' href='" +
					// request.getContextRoot() + "/"
					// + pageFactory.getPageName() + "/chord/" + c + "'>" + c +
					// "</a>";
					String chordLink = "<span style='cursor: pointer;' onclick='grass.chords.show(\"" + c + "\")'>" + c
							+ "</span>";
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

		JavaScript.getCurrent().addFunction("grass.chords.show", arguments -> {
			tabSheet.setSelectedTab(1);
			chordsTab.selectChord(arguments.getString(0));
		});
	}

}