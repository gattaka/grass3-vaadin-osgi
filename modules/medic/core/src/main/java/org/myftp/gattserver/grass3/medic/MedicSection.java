package org.myftp.gattserver.grass3.medic;

import java.util.Set;

import javax.annotation.Resource;

import org.myftp.gattserver.grass3.pages.factories.template.IPageFactory;
import org.myftp.gattserver.grass3.security.Role;
import org.myftp.gattserver.grass3.service.ISectionService;
import org.springframework.stereotype.Component;

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
