package cz.gattserver.grass3.hw;

import java.util.Set;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import cz.gattserver.grass3.pages.factories.template.IPageFactory;
import cz.gattserver.grass3.security.Role;
import cz.gattserver.grass3.service.ISectionService;

@Component("hwSection")
public class HWSection implements ISectionService {

	@Resource(name = "hwPageFactory")
	private IPageFactory hwPageFactory;

	public boolean isVisibleForRoles(Set<Role> roles) {
		if (roles == null)
			return false;
		return roles.contains(Role.ADMIN);
	}

	public IPageFactory getSectionPageFactory() {
		return hwPageFactory;
	}

	public String getSectionCaption() {
		return "Evidence HW";
	}

}
