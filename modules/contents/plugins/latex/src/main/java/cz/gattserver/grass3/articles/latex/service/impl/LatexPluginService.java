package cz.gattserver.grass3.articles.latex.service.impl;

import org.springframework.stereotype.Component;

import cz.gattserver.grass3.articles.editor.api.EditorButtonResources;
import cz.gattserver.grass3.articles.latex.plugin.LatexFactory;
import cz.gattserver.grass3.articles.parser.interfaces.PluginFactory;
import cz.gattserver.grass3.articles.service.PluginService;

@Component
public class LatexPluginService implements PluginService {

	private LatexFactory factory = new LatexFactory();

	public PluginFactory getPluginFactory() {
		return factory;
	}

	public EditorButtonResources getEditorButtonResources() {
		return factory.getEditorButtonResources();
	}

}
