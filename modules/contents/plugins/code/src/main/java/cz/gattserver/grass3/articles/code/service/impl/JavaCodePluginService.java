package cz.gattserver.grass3.articles.code.service.impl;

import org.springframework.stereotype.Component;

import cz.gattserver.grass3.articles.code.CodeFactory;
import cz.gattserver.grass3.articles.editor.api.EditorButtonResources;
import cz.gattserver.grass3.articles.parser.interfaces.IPluginFactory;
import cz.gattserver.grass3.articles.service.IPluginService;

@Component
public class JavaCodePluginService implements IPluginService {

	private CodeFactory factory = new CodeFactory("JAVA","Java","java.png","codemirror_java","clike.js");

	public IPluginFactory getPluginFactory() {
		return factory;
	}

	public EditorButtonResources getEditorButtonResources() {
		return factory.getEditorButtonResources();
	}

}