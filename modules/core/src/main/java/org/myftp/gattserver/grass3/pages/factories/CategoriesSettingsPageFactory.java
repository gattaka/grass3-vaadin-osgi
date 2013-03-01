package org.myftp.gattserver.grass3.pages.factories;

import java.util.Set;

import org.myftp.gattserver.grass3.pages.factories.template.AbstractSettingsPageFactory;
import org.myftp.gattserver.grass3.security.Role;
import org.springframework.stereotype.Component;

@Component("categoriesSettingsPageFactory")
public class CategoriesSettingsPageFactory extends AbstractSettingsPageFactory {

	public CategoriesSettingsPageFactory() {
		super("categories", "categoriesSettingsPage");
	}

	@Override
	public String getSettingsCaption() {
		return "Kategorie";
	}

	@Override
	public boolean isVisibleForRoles(Set<Role> roles) {
		return roles.contains(Role.ADMIN);
	}

}
