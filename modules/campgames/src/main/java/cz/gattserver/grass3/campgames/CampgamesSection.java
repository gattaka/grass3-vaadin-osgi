package cz.gattserver.grass3.campgames;

import java.util.Set;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import cz.gattserver.grass3.modules.SectionService;
import cz.gattserver.grass3.security.Role;
import cz.gattserver.grass3.ui.pages.factories.template.PageFactory;

@Component("campgamesSection")
public class CampgamesSection implements SectionService {

	@Resource(name = "campgamesPageFactory")
	private PageFactory campgamesPageFactory;

	public boolean isVisibleForRoles(Set<Role> roles) {
		if (roles == null)
			return false;
		return roles.contains(Role.ADMIN);
	}

	public PageFactory getSectionPageFactory() {
		return campgamesPageFactory;
	}

	public String getSectionCaption() {
		return "Hry";
	}

}
