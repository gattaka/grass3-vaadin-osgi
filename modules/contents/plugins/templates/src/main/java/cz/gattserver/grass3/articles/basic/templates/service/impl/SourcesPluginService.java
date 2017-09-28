package cz.gattserver.grass3.articles.basic.templates.service.impl;

import org.springframework.stereotype.Component;

import cz.gattserver.grass3.articles.basic.templates.sources.SourcesFactory;
import cz.gattserver.grass3.articles.editor.api.EditorButtonResources;
import cz.gattserver.grass3.articles.parser.interfaces.PluginFactory;
import cz.gattserver.grass3.articles.service.PluginService;

@Component
public class SourcesPluginService implements PluginService {

	private SourcesFactory factory = new SourcesFactory();

	public PluginFactory getPluginFactory() {
		return factory;
	}

	public EditorButtonResources getEditorButtonResources() {
		return factory.getEditorButtonResources();
	}

}
