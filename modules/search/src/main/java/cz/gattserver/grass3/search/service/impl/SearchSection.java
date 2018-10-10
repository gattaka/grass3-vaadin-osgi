package cz.gattserver.grass3.search.service.impl;

import java.util.Set;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import cz.gattserver.grass3.modules.SectionService;
import cz.gattserver.grass3.security.Role;
import cz.gattserver.grass3.ui.pages.factories.template.PageFactory;

@Component("searchSection")
public class SearchSection implements SectionService {

	@Resource(name = "searchPageFactory")
	private PageFactory searchPageFactory;

	public boolean isVisibleForRoles(Set<Role> roles) {
		// odkaz na sekci vyhledávání je viditelný všem rolím
		return true;
	}

	public String getSectionCaption() {
		return "Vyhledávání";
	}

	public PageFactory getSectionPageFactory() {
		return searchPageFactory;
	}

	@Override
	public Role[] getSectionRoles() {
		return new Role[0];
	}

}
