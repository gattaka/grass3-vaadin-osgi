package cz.gattserver.grass3.tabs.factories;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cz.gattserver.grass3.security.CoreACL;
import cz.gattserver.grass3.tabs.NodesSettingsPage;
import cz.gattserver.grass3.tabs.factories.template.AbstractModuleSettingsPageFactory;
import cz.gattserver.grass3.tabs.template.ModuleSettingsPage;
import cz.gattserver.grass3.ui.util.GrassRequest;

@Component("categoriesSettingsTabFactory")
public class CategoriesSettingsTabFactory extends AbstractModuleSettingsPageFactory {

	@Autowired
	private CoreACL coreACL;

	public CategoriesSettingsTabFactory() {
		super("Kategorie", "categories");
	}

	public boolean isAuthorized() {
		return coreACL.canShowCategoriesSettings(getUser());
	}

	@Override
	protected ModuleSettingsPage createPage(GrassRequest request) {
		return new NodesSettingsPage(request);
	}

}
