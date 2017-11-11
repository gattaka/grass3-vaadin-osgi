package cz.gattserver.grass3.hw.web;

import org.springframework.stereotype.Component;

import cz.gattserver.grass3.pages.factories.template.AbstractPageFactory;
import cz.gattserver.grass3.pages.template.GrassPage;
import cz.gattserver.grass3.server.GrassRequest;

@Component("hwPageFactory")
public class HWPageFactory extends AbstractPageFactory {

	private static final long serialVersionUID = -8008345071231675889L;

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
	protected GrassPage createPage(GrassRequest request) {
		return new HWPage(request);
	}
}
