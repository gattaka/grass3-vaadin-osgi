package cz.gattserver.grass3.articles.basic.service.impl;

import org.springframework.stereotype.Component;

import cz.gattserver.grass3.articles.basic.image.ImageFactory;
import cz.gattserver.grass3.articles.editor.api.EditorButtonResources;
import cz.gattserver.grass3.articles.parser.interfaces.PluginFactory;
import cz.gattserver.grass3.articles.service.PluginService;

@Component
public class Header1PluginService implements PluginService {

	private ImageFactory factory = new ImageFactory();

	public PluginFactory getPluginFactory() {
		return factory;
	}

	public EditorButtonResources getEditorButtonResources() {
		return factory.getEditorButtonResources();
	}

}
