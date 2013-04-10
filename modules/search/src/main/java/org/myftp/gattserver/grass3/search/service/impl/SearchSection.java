package org.myftp.gattserver.grass3.search.service.impl;

import java.util.Set;

import javax.annotation.Resource;

import org.myftp.gattserver.grass3.pages.factories.template.IPageFactory;
import org.myftp.gattserver.grass3.security.Role;
import org.myftp.gattserver.grass3.service.ISectionService;
import org.springframework.stereotype.Component;

@Component("searchSection")
public class SearchSection implements ISectionService {

	@Resource(name = "searchPageFactory")
	private IPageFactory searchPageFactory;

	public boolean isVisibleForRoles(Set<Role> roles) {
		return true;
	}

	public String getSectionCaption() {
		return "Vyhledávání";
	}

	public IPageFactory getSectionPageFactory() {
		return searchPageFactory;
	}

}
