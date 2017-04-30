package cz.gattserver.grass3.recipes;

import java.util.Set;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import cz.gattserver.grass3.pages.factories.template.IPageFactory;
import cz.gattserver.grass3.security.Role;
import cz.gattserver.grass3.service.ISectionService;

@Component("recipesSection")
public class RecipesSection implements ISectionService {

	@Resource(name = "recipesPageFactory")
	private IPageFactory recipesPageFactory;

	public boolean isVisibleForRoles(Set<Role> roles) {
		if (roles == null)
			return false;
		return roles.contains(Role.ADMIN);
	}

	public IPageFactory getSectionPageFactory() {
		return recipesPageFactory;
	}

	public String getSectionCaption() {
		return "Recepty";
	}

}