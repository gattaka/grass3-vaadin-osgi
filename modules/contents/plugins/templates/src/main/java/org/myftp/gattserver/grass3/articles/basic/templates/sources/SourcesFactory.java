package org.myftp.gattserver.grass3.articles.basic.templates.sources;

import org.myftp.gattserver.grass3.articles.editor.api.EditorButtonResources;
import org.myftp.gattserver.grass3.articles.parser.interfaces.AbstractParserPlugin;
import org.myftp.gattserver.grass3.articles.parser.interfaces.IPluginFactory;

/**
 * 
 * @author gatt
 */
public class SourcesFactory implements IPluginFactory {

	private final String tag = "SOURCES";

	public String getTag() {
		return tag;
	}

	public AbstractParserPlugin getPluginParser() {
		return new SourcesElement(tag);
	}

	public SourcesFactory() {
	}

	public EditorButtonResources getEditorButtonResources() {
		EditorButtonResources resources = new EditorButtonResources(tag);
		resources.setDescription("");
		resources.setPrefix("[" + tag + "]");
		resources.setSuffix("[/" + tag + "]");
		resources.setTagFamily("Šablony");
		return resources;
	}
}
