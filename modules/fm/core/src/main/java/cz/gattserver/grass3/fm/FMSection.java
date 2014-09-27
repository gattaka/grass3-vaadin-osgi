package cz.gattserver.grass3.fm;

import java.util.Set;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import cz.gattserver.grass3.pages.factories.template.IPageFactory;
import cz.gattserver.grass3.security.Role;
import cz.gattserver.grass3.service.ISectionService;

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
