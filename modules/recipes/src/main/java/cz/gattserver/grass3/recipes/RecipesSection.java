package cz.gattserver.grass3.recipes;

import java.util.Set;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import cz.gattserver.grass3.modules.SectionService;
import cz.gattserver.grass3.security.Role;
import cz.gattserver.grass3.ui.pages.factories.template.PageFactory;

@Component("recipesSection")
public class RecipesSection implements SectionService {

	@Resource(name = "recipesPageFactory")
	private PageFactory recipesPageFactory;

	public boolean isVisibleForRoles(Set<Role> roles) {
		if (roles == null)
			return false;
		return roles.contains(Role.ADMIN);
	}

	public PageFactory getSectionPageFactory() {
		return recipesPageFactory;
	}

	public String getSectionCaption() {
		return "Recepty";
	}

}
