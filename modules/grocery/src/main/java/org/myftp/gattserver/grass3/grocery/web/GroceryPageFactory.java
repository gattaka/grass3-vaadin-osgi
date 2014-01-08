package org.myftp.gattserver.grass3.grocery.web;

import org.myftp.gattserver.grass3.pages.factories.template.AbstractPageFactory;
import org.myftp.gattserver.grass3.security.Role;
import org.springframework.stereotype.Component;

@Component("groceryPageFactory")
public class GroceryPageFactory extends AbstractPageFactory {

	private static final long serialVersionUID = -8908780077374660264L;

	public GroceryPageFactory() {
		super("grocery", "groceryPage");
	}

	@Override
	protected boolean isAuthorized() {
		if (getUser() == null)
			return false;
		return getUser().getRoles().contains(Role.ADMIN);
		// return true;
	}
}
