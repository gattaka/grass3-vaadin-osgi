package cz.gattserver.grass3.articles.basic.templates.sources;

import cz.gattserver.grass3.articles.editor.api.EditorButtonResources;
import cz.gattserver.grass3.articles.parser.interfaces.AbstractParserPlugin;
import cz.gattserver.grass3.articles.parser.interfaces.IPluginFactory;

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
		resources.setDescription("Zdroje"); 
		resources.setPrefix("[" + tag + "]");
		resources.setSuffix("[/" + tag + "]");
		resources.setTagFamily("Šablony");
		return resources;
	}
}
