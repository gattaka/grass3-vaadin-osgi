package org.myftp.gattserver.grass3.articles.latex.web;

import java.util.Set;

import org.myftp.gattserver.grass3.security.Role;
import org.myftp.gattserver.grass3.tabs.factories.template.SettingsTabFactory;
import org.springframework.stereotype.Component;

@Component("latexSettingsTabFactory")
public class LatexSettingsTabFactory extends SettingsTabFactory {

	public LatexSettingsTabFactory() {
		super("LaTeX", "latex", "latexSettingsTab");
	}
	
	public boolean isVisibleForRoles(Set<Role> roles) {
		return roles.contains(Role.ADMIN);
	}
}
