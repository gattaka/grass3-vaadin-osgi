package cz.gattserver.grass3.articles.basic.service.impl;

import org.springframework.stereotype.Component;

import cz.gattserver.grass3.articles.basic.style.MonospaceFactory;
import cz.gattserver.grass3.articles.editor.api.EditorButtonResources;
import cz.gattserver.grass3.articles.parser.interfaces.PluginFactory;
import cz.gattserver.grass3.articles.service.PluginService;

@Component
public class MonospacePluginService implements PluginService {

	private MonospaceFactory factory = new MonospaceFactory();

	public PluginFactory getPluginFactory() {
		return factory;
	}

	public EditorButtonResources getEditorButtonResources() {
		return factory.getEditorButtonResources();
	}

}
