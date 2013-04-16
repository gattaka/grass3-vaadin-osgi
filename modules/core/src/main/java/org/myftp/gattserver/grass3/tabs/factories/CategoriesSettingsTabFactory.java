package org.myftp.gattserver.grass3.tabs.factories;

import javax.annotation.Resource;

import org.myftp.gattserver.grass3.security.ICoreACL;
import org.myftp.gattserver.grass3.tabs.factories.template.AbstractSettingsTabFactory;
import org.springframework.stereotype.Component;

@Component("categoriesSettingsTabFactory")
public class CategoriesSettingsTabFactory extends AbstractSettingsTabFactory {

	@Resource(name = "coreACL")
	private ICoreACL coreACL;

	public CategoriesSettingsTabFactory() {
		super("Kategorie", "categories", "categoriesSettingsTab");
	}

	@Override
	protected boolean isAuthorized() {
		return coreACL.canShowCategoriesSettings(getUser());
	}

}
