package org.myftp.gattserver.grass3.articles.code.service.impl;

import org.myftp.gattserver.grass3.articles.code.CodeFactory;
import org.myftp.gattserver.grass3.articles.editor.api.EditorButtonResources;
import org.myftp.gattserver.grass3.articles.parser.interfaces.IPluginFactory;
import org.myftp.gattserver.grass3.articles.service.IPluginService;
import org.springframework.stereotype.Component;

@Component
public class GroovyCodePluginService implements IPluginService {

	private CodeFactory factory = new CodeFactory("GROOVY", "Groovy",
			"groovy.jpeg", "codemirror_groovy");

	public IPluginFactory getPluginFactory() {
		return factory;
	}

	public EditorButtonResources getEditorButtonResources() {
		return factory.getEditorButtonResources();
	}

}