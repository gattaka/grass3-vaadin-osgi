package org.myftp.gattserver.grass3.pages.factories.template;

import java.util.Set;

import org.myftp.gattserver.grass3.pages.template.GrassLayout;
import org.myftp.gattserver.grass3.security.Role;
import org.myftp.gattserver.grass3.util.GrassRequest;

public interface IPageFactory {

	public String getPageName();

	public GrassLayout createPage(GrassRequest request);

	public boolean isVisibleForRoles(Set<Role> roles);

}
