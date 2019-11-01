package cz.gattserver.grass3.ui.pages.err.factories;

import org.springframework.stereotype.Component;

import cz.gattserver.grass3.ui.pages.err.Error500Page;
import cz.gattserver.grass3.ui.pages.factories.template.AbstractPageFactory;
import cz.gattserver.grass3.ui.pages.template.GrassPage;

@Component("err500PageFactory")
public class Error500PageFactory extends AbstractPageFactory {

	public Error500PageFactory() {
		super("err500");
	}

	@Override
	protected boolean isAuthorized() {
		return true;
	}

	@Override
	protected GrassPage createPage() {
		return new Error500Page();
	}
}
