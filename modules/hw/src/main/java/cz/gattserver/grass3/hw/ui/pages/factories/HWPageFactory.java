package cz.gattserver.grass3.hw.ui.pages.factories;

import org.springframework.stereotype.Component;

import cz.gattserver.grass3.hw.ui.pages.HWPage;
import cz.gattserver.grass3.ui.pages.factories.template.AbstractPageFactory;
import cz.gattserver.grass3.ui.pages.template.GrassPage;

@Component("hwPageFactory")
public class HWPageFactory extends AbstractPageFactory {

	public HWPageFactory() {
		super("hw");
	}

	@Override
	protected boolean isAuthorized() {
		if (getUser() == null)
			return false;
		return getUser().isAdmin();
	}

	@Override
	protected GrassPage createPage() {
		return new HWPage();
	}
}
