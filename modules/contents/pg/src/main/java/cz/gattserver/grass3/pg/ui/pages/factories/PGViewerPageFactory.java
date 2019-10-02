package cz.gattserver.grass3.pg.ui.pages.factories;

import org.springframework.stereotype.Component;

import cz.gattserver.grass3.pg.ui.pages.PGViewerPage;
import cz.gattserver.grass3.ui.pages.factories.template.AbstractPageFactory;
import cz.gattserver.grass3.ui.pages.template.GrassPage;

@Component("pgViewerPageFactory")
public class PGViewerPageFactory extends AbstractPageFactory {

	public PGViewerPageFactory() {
		super("photogallery");
	}

	@Override
	protected boolean isAuthorized() {
		return true;
	}

	@Override
	protected GrassPage createPage() {
		return new PGViewerPage();
	}
}
