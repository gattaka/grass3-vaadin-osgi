package org.myftp.gattserver.grass3.hw;

import java.util.Set;

import javax.annotation.Resource;

import org.myftp.gattserver.grass3.pages.factories.template.IPageFactory;
import org.myftp.gattserver.grass3.security.Role;
import org.myftp.gattserver.grass3.service.ISectionService;
import org.springframework.stereotype.Component;

@Component("hwSection")
public class HWSection implements ISectionService {

	@Resource(name = "hwPageFactory")
	private IPageFactory hwPageFactory;

	public boolean isVisibleForRoles(Set<Role> roles) {
		if (roles == null)
			return false;
		return roles.contains(Role.ADMIN) ;
	}

	public IPageFactory getSectionPageFactory() {
		return hwPageFactory;
	}

	public String getSectionCaption() {
		return "Evidence HW";
	}

}