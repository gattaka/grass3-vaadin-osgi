package org.myftp.gattserver.grass3.fm.web.factories;

import org.myftp.gattserver.grass3.fm.web.FMSettingsTab;
import org.myftp.gattserver.grass3.security.Role;
import org.myftp.gattserver.grass3.tabs.factories.template.AbstractSettingsTabFactory;
import org.myftp.gattserver.grass3.tabs.template.ISettingsTab;
import org.myftp.gattserver.grass3.ui.util.GrassRequest;
import org.springframework.stereotype.Component;

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
