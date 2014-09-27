package cz.gattserver.grass3.search.service.impl;

import java.util.Set;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import cz.gattserver.grass3.pages.factories.template.IPageFactory;
import cz.gattserver.grass3.security.Role;
import cz.gattserver.grass3.service.ISectionService;

@Component("searchSection")
public class SearchSection implements ISectionService {

	@Resource(name = "searchPageFactory")
	private IPageFactory searchPageFactory;

	public boolean isVisibleForRoles(Set<Role> roles) {
		// odkaz na sekci vyhledávání je viditelný všem rolím
		return true;
	}

	public String getSectionCaption() {
		return "Vyhledávání";
	}

	public IPageFactory getSectionPageFactory() {
		return searchPageFactory;
	}

}
