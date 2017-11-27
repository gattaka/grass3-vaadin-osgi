package cz.gattserver.grass3.ui.pages.err.factories;

import org.springframework.stereotype.Component;

import cz.gattserver.grass3.server.GrassRequest;
import cz.gattserver.grass3.ui.pages.err.Error404Page;
import cz.gattserver.grass3.ui.pages.factories.template.AbstractPageFactory;
import cz.gattserver.grass3.ui.pages.template.GrassPage;

@Component("err404PageFactory")
public class Error404PageFactory extends AbstractPageFactory {

	public Error404PageFactory() {
		super("err404");
	}

	@Override
	protected boolean isAuthorized() {
		return true;
	}

	@Override
	protected GrassPage createPage(GrassRequest request) {
		return new Error404Page(request);
	}
}
