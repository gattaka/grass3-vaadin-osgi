package cz.gattserver.grass3.pages.err.factories;

import org.springframework.stereotype.Component;

import cz.gattserver.grass3.pages.err.Err404Page;
import cz.gattserver.grass3.pages.factories.template.AbstractPageFactory;
import cz.gattserver.grass3.pages.template.GrassPage;
import cz.gattserver.grass3.ui.util.GrassRequest;

@Component("err404Factory")
public class Err404Factory extends AbstractPageFactory {

	private static final long serialVersionUID = 1538011525952923446L;

	public Err404Factory() {
		super("err404");
	}

	@Override
	protected boolean isAuthorized() {
		return true;
	}

	@Override
	protected GrassPage createPage(GrassRequest request) {
		return new Err404Page(request);
	}
}
