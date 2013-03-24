package org.myftp.gattserver.grass3.articles.container.service.impl;

import org.myftp.gattserver.grass3.articles.container.plugin.ContainerFactory;
import org.myftp.gattserver.grass3.articles.editor.api.EditorButtonResources;
import org.myftp.gattserver.grass3.articles.parser.interfaces.IPluginFactory;
import org.myftp.gattserver.grass3.articles.service.IPluginService;
import org.springframework.stereotype.Component;

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
