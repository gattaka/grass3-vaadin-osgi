package cz.gattserver.grass3.articles.basic.service.impl;

import org.springframework.stereotype.Component;

import cz.gattserver.grass3.articles.basic.style.CrossoutFactory;
import cz.gattserver.grass3.articles.editor.api.EditorButtonResources;
import cz.gattserver.grass3.articles.parser.interfaces.PluginFactory;
import cz.gattserver.grass3.articles.service.PluginService;

@Component
public class CrossoutPluginService implements PluginService {

	private CrossoutFactory factory = new CrossoutFactory();

	public PluginFactory getPluginFactory() {
		return factory;
	}

	public EditorButtonResources getEditorButtonResources() {
		return factory.getEditorButtonResources();
	}

}
