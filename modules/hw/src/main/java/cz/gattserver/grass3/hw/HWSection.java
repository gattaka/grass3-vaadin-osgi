package cz.gattserver.grass3.hw;

import java.util.Set;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import cz.gattserver.grass3.pages.factories.template.PageFactory;
import cz.gattserver.grass3.security.Role;
import cz.gattserver.grass3.service.SectionService;

@Component("hwSection")
public class HWSection implements SectionService {

	@Resource(name = "hwPageFactory")
	private PageFactory hwPageFactory;

	public boolean isVisibleForRoles(Set<Role> roles) {
		if (roles == null)
			return false;
		return roles.contains(Role.ADMIN);
	}

	public PageFactory getSectionPageFactory() {
		return hwPageFactory;
	}

	public String getSectionCaption() {
		return "Evidence HW";
	}

}
