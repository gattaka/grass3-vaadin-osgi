package cz.gattserver.grass3.monitor;

import java.util.Set;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import cz.gattserver.grass3.pages.factories.template.PageFactory;
import cz.gattserver.grass3.security.Role;
import cz.gattserver.grass3.service.SectionService;

@Component("monitorSection")
public class MonitorSection implements SectionService {

	@Resource(name = "monitorPageFactory")
	private PageFactory monitorPageFactory;

	public boolean isVisibleForRoles(Set<Role> roles) {
		if (roles == null)
			return false;
		return roles.contains(Role.ADMIN);
	}

	public PageFactory getSectionPageFactory() {
		return monitorPageFactory;
	}

	public String getSectionCaption() {
		return "System monitor";
	}

}
