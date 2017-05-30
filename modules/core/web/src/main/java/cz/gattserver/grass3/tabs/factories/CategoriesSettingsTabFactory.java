package cz.gattserver.grass3.tabs.factories;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import cz.gattserver.grass3.security.ICoreACL;
import cz.gattserver.grass3.tabs.NodesSettingsTab;
import cz.gattserver.grass3.tabs.factories.template.AbstractSettingsTabFactory;
import cz.gattserver.grass3.tabs.template.ISettingsTab;
import cz.gattserver.grass3.ui.util.GrassRequest;

@Component("categoriesSettingsTabFactory")
public class CategoriesSettingsTabFactory extends AbstractSettingsTabFactory {

	@Resource(name = "coreACL")
	private ICoreACL coreACL;

	public CategoriesSettingsTabFactory() {
		super("Kategorie", "categories");
	}

	public boolean isAuthorized() {
		return coreACL.canShowCategoriesSettings(getUser());
	}

	@Override
	protected ISettingsTab createTab(GrassRequest request) {
		return new NodesSettingsTab(request);
	}

}
