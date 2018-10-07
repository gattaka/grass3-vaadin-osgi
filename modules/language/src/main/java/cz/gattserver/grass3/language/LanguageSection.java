package cz.gattserver.grass3.language;

import java.util.Set;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import cz.gattserver.grass3.modules.SectionService;
import cz.gattserver.grass3.security.Role;
import cz.gattserver.grass3.ui.pages.factories.template.PageFactory;

@Component("languageSection")
public class LanguageSection implements SectionService {

	@Resource(name = "languagePageFactory")
	private PageFactory languagePageFactory;

	public boolean isVisibleForRoles(Set<Role> roles) {
		return true;
	}

	public PageFactory getSectionPageFactory() {
		return languagePageFactory;
	}

	public String getSectionCaption() {
		return "Jazyky";
	}

	@Override
	public Role[] getSectionRoles() {
		return null;
	}

}
