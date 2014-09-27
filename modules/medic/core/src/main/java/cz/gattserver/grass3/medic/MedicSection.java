package cz.gattserver.grass3.medic;

import java.util.Set;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import cz.gattserver.grass3.pages.factories.template.IPageFactory;
import cz.gattserver.grass3.security.Role;
import cz.gattserver.grass3.service.ISectionService;

@Component("medicSection")
public class MedicSection implements ISectionService {

	@Resource(name = "medicPageFactory")
	private IPageFactory medicPageFactory;

	public boolean isVisibleForRoles(Set<Role> roles) {
		if (roles == null)
			return false;
		return roles.contains(Role.ADMIN) ;
	}

	public IPageFactory getSectionPageFactory() {
		return medicPageFactory;
	}

	public String getSectionCaption() {
		return "Zdravotní zápisník";
	}

}
