package org.myftp.gattserver.grass3.tabs.factories;

import java.util.Set;

import org.myftp.gattserver.grass3.pages.factories.template.SettingsTabFactory;
import org.myftp.gattserver.grass3.security.Role;
import org.springframework.stereotype.Component;

@Component("categoriesSettingsTabFactory")
public class CategoriesSettingsTabFactory extends SettingsTabFactory {

	public CategoriesSettingsTabFactory() {
		super("Kategorie", "categories", "categoriesSettingsTab");
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
