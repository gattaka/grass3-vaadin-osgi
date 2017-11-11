package cz.gattserver.grass3.pages.settings.factories;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cz.gattserver.grass3.pages.settings.ModuleSettingsPage;
import cz.gattserver.grass3.pages.settings.NodesSettingsPage;
import cz.gattserver.grass3.security.CoreACL;
import cz.gattserver.grass3.server.GrassRequest;

@Component("nodesSettingsPageFactory")
public class NodesSettingsPageFactory extends AbstractModuleSettingsPageFactory {

	@Autowired
	private CoreACL coreACL;

	public NodesSettingsPageFactory() {
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