package cz.gattserver.grass3.ui.pages.err.factories;

import org.springframework.stereotype.Component;

import cz.gattserver.grass3.server.GrassRequest;
import cz.gattserver.grass3.ui.pages.err.Error500Page;
import cz.gattserver.grass3.ui.pages.factories.template.AbstractPageFactory;
import cz.gattserver.grass3.ui.pages.template.GrassPage;

@Component("err500PageFactory")
public class Error500PageFactory extends AbstractPageFactory {

	private static final long serialVersionUID = -8588396224579592218L;

	public Error500PageFactory() {
		super("err500");
	}

	@Override
	protected boolean isAuthorized() {
		return true;
	}

	@Override
	protected GrassPage createPage(GrassRequest request) {
		return new Error500Page(request);
	}
}
