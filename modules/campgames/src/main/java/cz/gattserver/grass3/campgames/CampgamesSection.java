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
		return true;
	}

	public PageFactory getSectionPageFactory() {
		return campgamesPageFactory;
	}

	public String getSectionCaption() {
		return "Hry";
	}

	@Override
	public Role[] getSectionRoles() {
		return CampgamesRole.values();
	}

}
