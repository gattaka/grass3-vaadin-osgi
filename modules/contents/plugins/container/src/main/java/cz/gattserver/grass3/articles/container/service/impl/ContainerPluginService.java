package cz.gattserver.grass3.articles.container.service.impl;

import org.springframework.stereotype.Component;

import cz.gattserver.grass3.articles.container.plugin.ContainerFactory;
import cz.gattserver.grass3.articles.editor.api.EditorButtonResources;
import cz.gattserver.grass3.articles.parser.interfaces.PluginFactory;
import cz.gattserver.grass3.articles.service.PluginService;

@Component
public class ContainerPluginService implements PluginService {

	private ContainerFactory factory = new ContainerFactory();

	public PluginFactory getPluginFactory() {
		return factory;
	}

	public EditorButtonResources getEditorButtonResources() {
		return factory.getEditorButtonResources();
	}

}
