package cz.gattserver.grass3.articles.code.service.impl;

import org.springframework.stereotype.Component;

import cz.gattserver.grass3.articles.code.CodeFactory;
import cz.gattserver.grass3.articles.editor.api.EditorButtonResources;
import cz.gattserver.grass3.articles.parser.interfaces.IPluginFactory;
import cz.gattserver.grass3.articles.service.IPluginService;

@Component
public class CCodePluginService implements IPluginService {

	private CodeFactory factory = new CodeFactory("C","C","","codemirror_c","clike.js");

	public IPluginFactory getPluginFactory() {
		return factory;
	}

	public EditorButtonResources getEditorButtonResources() {
		return factory.getEditorButtonResources();
	}

}
