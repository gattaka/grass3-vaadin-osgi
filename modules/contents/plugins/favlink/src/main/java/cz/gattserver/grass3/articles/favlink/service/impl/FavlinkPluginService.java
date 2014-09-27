package cz.gattserver.grass3.articles.favlink.service.impl;

import org.springframework.stereotype.Component;

import cz.gattserver.grass3.articles.editor.api.EditorButtonResources;
import cz.gattserver.grass3.articles.favlink.plugin.LinkFactory;
import cz.gattserver.grass3.articles.parser.interfaces.IPluginFactory;
import cz.gattserver.grass3.articles.service.IPluginService;

@Component
public class FavlinkPluginService implements IPluginService {

	private LinkFactory factory = new LinkFactory();

	public IPluginFactory getPluginFactory() {
		return factory;
	}

	public EditorButtonResources getEditorButtonResources() {
		return factory.getEditorButtonResources();
	}

}
