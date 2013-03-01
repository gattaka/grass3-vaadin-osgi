package org.myftp.gattserver.grass3.search.service.impl;

import java.util.Set;

import org.myftp.gattserver.grass3.pages.template.GrassWindow;
import org.myftp.gattserver.grass3.search.SearchWindow;
import org.myftp.gattserver.grass3.security.Role;
import org.myftp.gattserver.grass3.service.ISectionService;

public class SearchSection implements ISectionService {

	public Class<? extends GrassWindow> getSectionWindowClass() {
		return SearchWindow.class;
	}

	public GrassWindow getSectionWindowNewInstance() {
		return new SearchWindow();
	}

	public boolean isVisibleForRoles(Set<Role> roles) {
		return true;
	}

	public String getSectionCaption() {
		return "Vyhledávání";
	}

	public String getSectionIDName() {
		return SearchWindow.NAME;
	}

}
