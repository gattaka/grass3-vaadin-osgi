package org.myftp.gattserver.grass3.articles.code.service.impl;

import org.myftp.gattserver.grass3.articles.code.CodeFactory;
import org.myftp.gattserver.grass3.articles.code.HighlightEngine;
import org.myftp.gattserver.grass3.articles.editor.api.EditorButtonResources;
import org.myftp.gattserver.grass3.articles.parser.interfaces.IPluginFactory;
import org.myftp.gattserver.grass3.articles.service.IPluginService;
import org.springframework.stereotype.Component;

@Component
public class GroovyCodePluginService implements IPluginService {

	private CodeFactory factory = new CodeFactory("GROOVY","codemirror_groovy","Groovy","groovy.jpeg");

	public GroovyCodePluginService() {
		factory.setHighlightEngine(HighlightEngine.CODEMIRROR);
	}
	
	public IPluginFactory getPluginFactory() {
		return factory;
	}

	public EditorButtonResources getEditorButtonResources() {
		return factory.getEditorButtonResources();
	}

}
