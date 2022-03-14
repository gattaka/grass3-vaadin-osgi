package cz.gattserver.grass3.songs.ui;

import org.springframework.stereotype.Component;

import cz.gattserver.grass3.ui.pages.factories.template.AbstractPageFactory;

@Component("songsPageFactory")
public class SongsPageFactory extends AbstractPageFactory {

	public SongsPageFactory() {
		super("songs");
	}
}
