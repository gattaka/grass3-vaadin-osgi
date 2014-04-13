package org.myftp.gattserver.grass3.articles.basic.templates.service.impl;

import org.myftp.gattserver.grass3.articles.basic.templates.sources.SourcesFactory;
import org.myftp.gattserver.grass3.articles.editor.api.EditorButtonResources;
import org.myftp.gattserver.grass3.articles.parser.interfaces.IPluginFactory;
import org.myftp.gattserver.grass3.articles.service.IPluginService;
import org.springframework.stereotype.Component;

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
