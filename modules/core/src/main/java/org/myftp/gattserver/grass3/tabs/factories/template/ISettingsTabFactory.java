package org.myftp.gattserver.grass3.tabs.factories.template;

import java.util.Set;

import org.myftp.gattserver.grass3.pages.template.GrassLayout;
import org.myftp.gattserver.grass3.security.Role;
import org.myftp.gattserver.grass3.util.GrassRequest;

public interface ISettingsTabFactory {

	public String getSettingsCaption();

	public String getSettingsURL();

	public GrassLayout createPage(GrassRequest request);

	public boolean isVisibleForRoles(Set<Role> roles);

}
