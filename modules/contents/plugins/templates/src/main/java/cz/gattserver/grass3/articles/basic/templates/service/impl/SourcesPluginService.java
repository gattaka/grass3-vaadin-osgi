package cz.gattserver.grass3.articles.basic.templates.service.impl;

import org.springframework.stereotype.Component;

import cz.gattserver.grass3.articles.basic.templates.sources.SourcesFactory;
import cz.gattserver.grass3.articles.editor.api.EditorButtonResources;
import cz.gattserver.grass3.articles.parser.interfaces.IPluginFactory;
import cz.gattserver.grass3.articles.service.IPluginService;

@Component
public class SourcesPluginService implements IPluginService {

	private SourcesFactory factory = new SourcesFactory();

	public IPluginFactory getPluginFactory() {
		return factory;
	}

	public EditorButtonResources getEditorButtonResources() {
		return factory.getEditorButtonResources();
	}

}
