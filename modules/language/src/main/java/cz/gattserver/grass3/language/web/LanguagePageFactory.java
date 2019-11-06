package cz.gattserver.grass3.language.web;

import org.springframework.stereotype.Component;

import cz.gattserver.grass3.ui.pages.factories.template.AbstractPageFactory;

@Component("languagePageFactory")
public class LanguagePageFactory extends AbstractPageFactory {

	public LanguagePageFactory() {
		super("language");
	}

}
