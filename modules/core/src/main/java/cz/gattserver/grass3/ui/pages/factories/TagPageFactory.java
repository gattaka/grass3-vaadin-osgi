package cz.gattserver.grass3.ui.pages.factories;

import org.springframework.stereotype.Component;

import cz.gattserver.grass3.server.GrassRequest;
import cz.gattserver.grass3.ui.pages.TagPage;
import cz.gattserver.grass3.ui.pages.factories.template.AbstractPageFactory;
import cz.gattserver.grass3.ui.pages.template.GrassPage;

@Component(value = "tagPageFactory")
public class TagPageFactory extends AbstractPageFactory {

	private static final long serialVersionUID = -5921551750646968643L;

	public TagPageFactory() {
		super("tag");
	}

	@Override
	protected boolean isAuthorized() {
		return true;
	}

	@Override
	protected GrassPage createPage(GrassRequest request) {
		return new TagPage(request);
	}
}
