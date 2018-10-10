package cz.gattserver.grass3.drinks;

import java.util.Set;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import cz.gattserver.grass3.modules.SectionService;
import cz.gattserver.grass3.security.Role;
import cz.gattserver.grass3.ui.pages.factories.template.PageFactory;

@Component("drinksSection")
public class DrinksSection implements SectionService {

	@Resource(name = "drinksPageFactory")
	private PageFactory drinksPageFactory;

	public boolean isVisibleForRoles(Set<Role> roles) {
		return true;
	}

	public PageFactory getSectionPageFactory() {
		return drinksPageFactory;
	}

	public String getSectionCaption() {
		return "Nápoje";
	}

	@Override
	public Role[] getSectionRoles() {
		return new Role[0];
	}

}
