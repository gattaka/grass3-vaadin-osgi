package cz.gattserver.grass3.fm;

import java.util.Set;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import cz.gattserver.grass3.pages.factories.template.PageFactory;
import cz.gattserver.grass3.security.Role;
import cz.gattserver.grass3.service.SectionService;

@Component("fmSection")
public class FMSection implements SectionService {

	@Resource(name = "fmPageFactory")
	private PageFactory fmPageFactory;

	public boolean isVisibleForRoles(Set<Role> roles) {
		if (roles == null)
			return false;
		return roles.contains(Role.ADMIN) || roles.contains(Role.FRIEND);
	}

	public PageFactory getSectionPageFactory() {
		return fmPageFactory;
	}

	public String getSectionCaption() {
		return "Soubory";
	}

}
