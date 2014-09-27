package cz.gattserver.grass3.pages.err.factories;

import org.springframework.stereotype.Component;

import cz.gattserver.grass3.pages.err.Err403Page;
import cz.gattserver.grass3.pages.factories.template.AbstractPageFactory;
import cz.gattserver.grass3.pages.template.IGrassPage;
import cz.gattserver.grass3.ui.util.GrassRequest;

@Component("err403Factory")
public class Err403Factory extends AbstractPageFactory {

	private static final long serialVersionUID = 128945054380924993L;

	public Err403Factory() {
		super("err403");
	}

	@Override
	protected boolean isAuthorized() {
		return true;
	}

	@Override
	protected IGrassPage createPage(GrassRequest request) {
		return new Err403Page(request);
	}
}
