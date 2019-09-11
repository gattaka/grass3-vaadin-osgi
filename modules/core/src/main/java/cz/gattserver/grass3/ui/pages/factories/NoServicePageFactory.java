package cz.gattserver.grass3.ui.pages.factories;

import org.springframework.stereotype.Component;

import cz.gattserver.grass3.ui.pages.NoServicePage;
import cz.gattserver.grass3.ui.pages.factories.template.AbstractPageFactory;
import cz.gattserver.grass3.ui.pages.template.GrassPage;

@Component(value = "noServicePageFactory")
public class NoServicePageFactory extends AbstractPageFactory {

	public NoServicePageFactory() {
		super("noservice");
	}

	@Override
	protected boolean isAuthorized() {
		return true;
	}

	@Override
	protected GrassPage createPage() {
		return new NoServicePage();
	}
}
