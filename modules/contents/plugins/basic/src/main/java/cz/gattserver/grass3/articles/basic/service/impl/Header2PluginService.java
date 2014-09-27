package cz.gattserver.grass3.articles.basic.service.impl;

import org.springframework.stereotype.Component;

import cz.gattserver.grass3.articles.basic.headers.Header2Factory;
import cz.gattserver.grass3.articles.editor.api.EditorButtonResources;
import cz.gattserver.grass3.articles.parser.interfaces.IPluginFactory;
import cz.gattserver.grass3.articles.service.IPluginService;

@Component
public class Header2PluginService implements IPluginService {

	private Header2Factory factory = new Header2Factory();

	public IPluginFactory getPluginFactory() {
		return factory;
	}

	public EditorButtonResources getEditorButtonResources() {
		return factory.getEditorButtonResources();
	}

}
