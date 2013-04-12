package org.myftp.gattserver.grass3.tabs.factories;

import org.myftp.gattserver.grass3.tabs.factories.template.AbstractSettingsTabFactory;
import org.springframework.stereotype.Component;

@Component("categoriesSettingsTabFactory")
public class CategoriesSettingsTabFactory extends AbstractSettingsTabFactory {

	public CategoriesSettingsTabFactory() {
		super("Kategorie", "categories", "categoriesSettingsTab");
	}

	@Override
	protected boolean isAuthorized() {
		return getUserACL().canShowCategoriesSettings();
	}

}
