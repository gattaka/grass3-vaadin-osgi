package cz.gattserver.grass3.articles.basic.service.impl;

import org.springframework.stereotype.Component;

import cz.gattserver.grass3.articles.basic.style.CrossoutFactory;
import cz.gattserver.grass3.articles.editor.api.EditorButtonResources;
import cz.gattserver.grass3.articles.parser.interfaces.IPluginFactory;
import cz.gattserver.grass3.articles.service.IPluginService;

@Component
public class CrossoutPluginService implements IPluginService {

	private CrossoutFactory factory = new CrossoutFactory();

	public IPluginFactory getPluginFactory() {
		return factory;
	}

	public EditorButtonResources getEditorButtonResources() {
		return factory.getEditorButtonResources();
	}

}
