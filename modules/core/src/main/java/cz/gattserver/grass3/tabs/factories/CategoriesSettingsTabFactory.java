package cz.gattserver.grass3.tabs.factories;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cz.gattserver.grass3.security.CoreACL;
import cz.gattserver.grass3.tabs.NodesSettingsTab;
import cz.gattserver.grass3.tabs.factories.template.AbstractSettingsTabFactory;
import cz.gattserver.grass3.tabs.template.SettingsTab;
import cz.gattserver.grass3.ui.util.GrassRequest;

@Component("categoriesSettingsTabFactory")
public class CategoriesSettingsTabFactory extends AbstractSettingsTabFactory {

	@Autowired
	private CoreACL coreACL;

	public CategoriesSettingsTabFactory() {
		super("Kategorie", "categories");
	}

	public boolean isAuthorized() {
		return coreACL.canShowCategoriesSettings(getUser());
	}

	@Override
	protected SettingsTab createTab(GrassRequest request) {
		return new NodesSettingsTab(request);
	}

}