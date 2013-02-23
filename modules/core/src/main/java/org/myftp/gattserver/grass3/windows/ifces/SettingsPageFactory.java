package org.myftp.gattserver.grass3.windows.ifces;

import org.myftp.gattserver.grass3.util.GrassRequest;
import org.myftp.gattserver.grass3.windows.template.GrassPage;
import org.myftp.gattserver.grass3.windows.template.SettingsPage;

public abstract class SettingsPageFactory extends PageFactory {

	public SettingsPageFactory(String pageName) {
		super("settings/" + pageName);
	}

	@Override
	public GrassPage createPage(GrassRequest request) {
		return createSettingsPage(request);
	}

	public abstract SettingsPage createSettingsPage(GrassRequest request);

}
