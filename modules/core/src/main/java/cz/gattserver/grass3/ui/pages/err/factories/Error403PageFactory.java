package cz.gattserver.grass3.ui.pages.err.factories;

import org.springframework.stereotype.Component;

import cz.gattserver.grass3.server.GrassRequest;
import cz.gattserver.grass3.ui.pages.err.Error403Page;
import cz.gattserver.grass3.ui.pages.factories.template.AbstractPageFactory;
import cz.gattserver.grass3.ui.pages.template.GrassPage;

@Component("err403PageFactory")
public class Error403PageFactory extends AbstractPageFactory {

	private static final long serialVersionUID = 128945054380924993L;

	public Error403PageFactory() {
		super("err403");
	}

	@Override
	protected boolean isAuthorized() {
		return true;
	}

	@Override
	protected GrassPage createPage(GrassRequest request) {
		return new Error403Page(request);
	}
}
