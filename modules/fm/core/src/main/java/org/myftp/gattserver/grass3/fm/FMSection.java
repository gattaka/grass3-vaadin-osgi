package org.myftp.gattserver.grass3.fm;

import java.util.Set;

import javax.annotation.Resource;

import org.myftp.gattserver.grass3.pages.factories.template.IPageFactory;
import org.myftp.gattserver.grass3.security.Role;
import org.myftp.gattserver.grass3.service.ISectionService;
import org.springframework.stereotype.Component;

@Component("fmSection")
public class FMSection implements ISectionService {

	@Resource(name = "fmPageFactory")
	private IPageFactory fmPageFactory;

	public boolean isVisibleForRoles(Set<Role> roles) {
		if (roles == null)
			return false;
		return roles.contains(Role.ADMIN) || roles.contains(Role.FRIEND);
	}

	public IPageFactory getSectionPageFactory() {
		return fmPageFactory;
	}

	public String getSectionCaption() {
		return "Soubory";
	}

}
