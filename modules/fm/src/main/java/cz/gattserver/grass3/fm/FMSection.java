package cz.gattserver.grass3.fm;

import java.util.Set;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import cz.gattserver.grass3.modules.SectionService;
import cz.gattserver.grass3.security.CoreRole;
import cz.gattserver.grass3.security.Role;
import cz.gattserver.grass3.ui.pages.factories.template.PageFactory;

@Component("fmSection")
public class FMSection implements SectionService {

	@Resource(name = "fmPageFactory")
	private PageFactory fmPageFactory;

	public boolean isVisibleForRoles(Set<Role> roles) {
		if (roles == null)
			return false;
		return roles.contains(CoreRole.ADMIN) || roles.contains(CoreRole.FRIEND);
	}

	public PageFactory getSectionPageFactory() {
		return fmPageFactory;
	}

	public String getSectionCaption() {
		return "Soubory";
	}

	@Override
	public Role[] getSectionRoles() {
		return new Role[0];
	}

}
