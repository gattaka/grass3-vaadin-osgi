package cz.gattserver.grass3.articles.latex.service.impl;

import org.springframework.stereotype.Component;

import cz.gattserver.grass3.articles.editor.api.EditorButtonResources;
import cz.gattserver.grass3.articles.latex.plugin.LatexFactory;
import cz.gattserver.grass3.articles.parser.interfaces.IPluginFactory;
import cz.gattserver.grass3.articles.service.IPluginService;

@Component
public class LatexPluginService implements IPluginService {

	private LatexFactory factory = new LatexFactory();

	public IPluginFactory getPluginFactory() {
		return factory;
	}

	public EditorButtonResources getEditorButtonResources() {
		return factory.getEditorButtonResources();
	}

}
