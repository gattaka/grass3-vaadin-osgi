package cz.gattserver.grass3.language.web;

import org.springframework.stereotype.Component;

import cz.gattserver.grass3.ui.pages.factories.template.AbstractPageFactory;
import cz.gattserver.grass3.ui.pages.template.GrassPage;

@Component("languagePageFactory")
public class LanguagePageFactory extends AbstractPageFactory {

	public LanguagePageFactory() {
		super("language");
	}

	@Override
	protected boolean isAuthorized() {
		return true;
	}

	@Override
	protected GrassPage createPage() {
		return new LanguagePage();
	}
}
