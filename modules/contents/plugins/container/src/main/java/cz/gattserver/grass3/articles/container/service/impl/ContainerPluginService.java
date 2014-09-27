package cz.gattserver.grass3.articles.container.service.impl;

import org.springframework.stereotype.Component;

import cz.gattserver.grass3.articles.container.plugin.ContainerFactory;
import cz.gattserver.grass3.articles.editor.api.EditorButtonResources;
import cz.gattserver.grass3.articles.parser.interfaces.IPluginFactory;
import cz.gattserver.grass3.articles.service.IPluginService;

@Component
public class ContainerPluginService implements IPluginService {

	private ContainerFactory factory = new ContainerFactory();

	public IPluginFactory getPluginFactory() {
		return factory;
	}

	public EditorButtonResources getEditorButtonResources() {
		return factory.getEditorButtonResources();
	}

}
