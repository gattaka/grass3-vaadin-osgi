package org.myftp.gattserver.grass3.articles.basic.service.impl;

import org.myftp.gattserver.grass3.articles.basic.headers.Header3Factory;
import org.myftp.gattserver.grass3.articles.editor.api.EditorButtonResources;
import org.myftp.gattserver.grass3.articles.parser.interfaces.IPluginFactory;
import org.myftp.gattserver.grass3.articles.service.IPluginService;
import org.springframework.stereotype.Component;

@Component
public class Header3PluginService implements IPluginService {

	private Header3Factory factory = new Header3Factory();

	public IPluginFactory getPluginFactory() {
		return factory;
	}

	public EditorButtonResources getEditorButtonResources() {
		return factory.getEditorButtonResources();
	}

}
