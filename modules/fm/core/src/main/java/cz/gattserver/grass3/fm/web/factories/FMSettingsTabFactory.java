package cz.gattserver.grass3.fm.web.factories;

import org.springframework.stereotype.Component;

import cz.gattserver.grass3.fm.web.FMSettingsTab;
import cz.gattserver.grass3.security.Role;
import cz.gattserver.grass3.tabs.factories.template.AbstractSettingsTabFactory;
import cz.gattserver.grass3.tabs.template.ISettingsTab;
import cz.gattserver.grass3.ui.util.GrassRequest;

@Component("fmSettingsTabFactory")
public class FMSettingsTabFactory extends AbstractSettingsTabFactory {

	public FMSettingsTabFactory() {
		super("Soubory", "fm");
	}

	public boolean isAuthorized() {
		if (getUser() == null)
			return false;
		return getUser().getRoles().contains(Role.ADMIN);
	}

	@Override
	protected ISettingsTab createTab(GrassRequest request) {
		return new FMSettingsTab(request) ;
	}
}
