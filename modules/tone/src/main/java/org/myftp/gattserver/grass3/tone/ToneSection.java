package org.myftp.gattserver.grass3.tone;

import java.util.Set;

import org.myftp.gattserver.grass3.security.Role;
import org.myftp.gattserver.grass3.service.ISectionService;
import org.myftp.gattserver.grass3.windows.template.GrassWindow;

public class ToneSection implements ISectionService {

	public Class<? extends GrassWindow> getSectionWindowClass() {
		return ToneWindow.class;
	}

	public GrassWindow getSectionWindowNewInstance() {
		return new ToneWindow();
	}

	public boolean isVisibleForRoles(Set<Role> roles) {
		return true;
	}

	public String getSectionCaption() {
		return "Tóny";
	}

	public String getSectionIDName() {
		return ToneWindow.NAME;
	}

}
