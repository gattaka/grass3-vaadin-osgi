package org.myftp.gattserver.grass3.monitor;

import java.util.Set;

import javax.annotation.Resource;

import org.myftp.gattserver.grass3.pages.factories.template.IPageFactory;
import org.myftp.gattserver.grass3.security.Role;
import org.myftp.gattserver.grass3.service.ISectionService;
import org.springframework.stereotype.Component;

@Component("monitorSection")
public class MonitorSection implements ISectionService {

	@Resource(name = "monitorPageFactory")
	private IPageFactory monitorPageFactory;

	public boolean isVisibleForRoles(Set<Role> roles) {
		if (roles == null)
			return false;
		return roles.contains(Role.ADMIN);
	}

	public IPageFactory getSectionPageFactory() {
		return monitorPageFactory;
	}

	public String getSectionCaption() {
		return "System monitor";
	}

}
