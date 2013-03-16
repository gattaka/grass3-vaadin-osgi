package org.myftp.gattserver.grass3.articles.basic.service.impl;

import org.myftp.gattserver.grass3.articles.basic.list.ListFactory;
import org.myftp.gattserver.grass3.articles.editor.api.EditorButtonResources;
import org.myftp.gattserver.grass3.articles.parser.interfaces.IPluginFactory;
import org.myftp.gattserver.grass3.articles.service.IPluginService;
import org.springframework.stereotype.Component;

@Component
public class UListPluginService implements IPluginService {

	private ListFactory factory = new ListFactory(false);

	public IPluginFactory getPluginFactory() {
		return factory;
	}

	public EditorButtonResources getEditorButtonResources() {
		return factory.getEditorButtonResources();
	}

}
