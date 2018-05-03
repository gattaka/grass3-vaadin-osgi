package cz.gattserver.grass3.songs.web;

import org.springframework.stereotype.Component;

import cz.gattserver.grass3.server.GrassRequest;
import cz.gattserver.grass3.ui.pages.factories.template.AbstractPageFactory;
import cz.gattserver.grass3.ui.pages.template.GrassPage;

@Component("songsPageFactory")
public class SongsPageFactory extends AbstractPageFactory {

	public SongsPageFactory() {
		super("songs");
	}

	@Override
	protected boolean isAuthorized() {
		return true;
	}

	@Override
	protected GrassPage createPage(GrassRequest request) {
		return new SongsPage(request);
	}
}
