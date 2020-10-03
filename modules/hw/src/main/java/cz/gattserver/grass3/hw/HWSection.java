package cz.gattserver.grass3.hw;

import java.util.Set;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import cz.gattserver.grass3.modules.SectionService;
import cz.gattserver.grass3.security.Role;
import cz.gattserver.grass3.ui.pages.factories.template.PageFactory;

@Component("hwSection")
public class HWSection implements SectionService {

	@Resource(name = "hwPageFactory")
	private PageFactory hwPageFactory;

	public boolean isVisibleForRoles(Set<Role> roles) {
		return true;
	}

	public PageFactory getSectionPageFactory() {
		return hwPageFactory;
	}

	public String getSectionCaption() {
		return "HW";
	}

	@Override
	public Role[] getSectionRoles() {
		return new Role[0];
	}

}
