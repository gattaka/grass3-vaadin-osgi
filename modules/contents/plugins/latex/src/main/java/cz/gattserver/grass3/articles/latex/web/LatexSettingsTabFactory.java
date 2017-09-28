package cz.gattserver.grass3.articles.latex.web;

import org.springframework.stereotype.Component;

import cz.gattserver.grass3.security.Role;
import cz.gattserver.grass3.tabs.factories.template.AbstractSettingsTabFactory;
import cz.gattserver.grass3.tabs.template.SettingsTab;
import cz.gattserver.grass3.ui.util.GrassRequest;

@Component("latexSettingsTabFactory")
public class LatexSettingsTabFactory extends AbstractSettingsTabFactory {

	public LatexSettingsTabFactory() {
		super("LaTeX", "latex");
	}

	public boolean isAuthorized() {
		if (getUser() == null)
			return false;
		return getUser().getRoles().contains(Role.ADMIN);
	}

	@Override
	protected SettingsTab createTab(GrassRequest request) {
		return new LatexSettingsTab(request);
	}
}
