package org.myftp.gattserver.grass3.medic.web;

import org.myftp.gattserver.grass3.pages.factories.template.AbstractPageFactory;
import org.myftp.gattserver.grass3.security.Role;
import org.springframework.stereotype.Component;

@Component("medicPageFactory")
public class MedicPageFactory extends AbstractPageFactory {

	private static final long serialVersionUID = 8984837128014801897L;

	public MedicPageFactory() {
		super("medic", "medicPage");
	}

	@Override
	protected boolean isAuthorized() {
		if (getUser() == null)
			return false;
		return getUser().getRoles().contains(Role.ADMIN);
		// return true;
	}
}
