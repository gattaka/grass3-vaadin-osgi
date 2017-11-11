package cz.gattserver.grass3.pages.factories;

import org.springframework.stereotype.Component;

import cz.gattserver.grass3.pages.NoServicePage;
import cz.gattserver.grass3.pages.factories.template.AbstractPageFactory;
import cz.gattserver.grass3.pages.template.GrassPage;
import cz.gattserver.grass3.server.GrassRequest;

@Component(value = "noServicePageFactory")
public class NoServicePageFactory extends AbstractPageFactory {

	private static final long serialVersionUID = -8393895002312736988L;

	public NoServicePageFactory() {
		super("noservice");
	}

	@Override
	protected boolean isAuthorized() {
		return true;
	}

	@Override
	protected GrassPage createPage(GrassRequest request) {
		return new NoServicePage(request);
	}
}
