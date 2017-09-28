package cz.gattserver.grass3.articles.favlink.service.impl;

import org.springframework.stereotype.Component;

import cz.gattserver.grass3.articles.editor.api.EditorButtonResources;
import cz.gattserver.grass3.articles.favlink.plugin.LinkFactory;
import cz.gattserver.grass3.articles.parser.interfaces.PluginFactory;
import cz.gattserver.grass3.articles.service.PluginService;

@Component
public class FavlinkPluginService implements PluginService {

	private LinkFactory factory = new LinkFactory();

	public PluginFactory getPluginFactory() {
		return factory;
	}

	public EditorButtonResources getEditorButtonResources() {
		return factory.getEditorButtonResources();
	}

}
