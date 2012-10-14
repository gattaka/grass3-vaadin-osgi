package org.myftp.gattserver.grass3.fm;

import java.util.Set;

import org.myftp.gattserver.grass3.security.Role;
import org.myftp.gattserver.grass3.service.ISectionService;
import org.myftp.gattserver.grass3.windows.template.GrassWindow;

public class FMSection implements ISectionService {

	public Class<? extends GrassWindow> getSectionWindowClass() {
		return FMWindow.class;
	}

	public GrassWindow getSectionWindowNewInstance() {
		return new FMWindow();
	}

	public boolean isVisibleForRoles(Set<Role> roles) {
		return true;
	}

	public String getSectionCaption() {
		return "Soubory";
	}

	public String getSectionIDName() {
		return FMWindow.NAME;
	}

}
