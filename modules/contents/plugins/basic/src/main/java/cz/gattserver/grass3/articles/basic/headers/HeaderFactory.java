package cz.gattserver.grass3.articles.basic.headers;

import cz.gattserver.grass3.articles.editor.api.EditorButtonResources;
import cz.gattserver.grass3.articles.parser.interfaces.AbstractParserPlugin;
import cz.gattserver.grass3.articles.parser.interfaces.PluginFactory;

/**
 * 
 * @author gatt
 */
public abstract class HeaderFactory implements PluginFactory {

	private String tagTemplate = "N";
	private String tag;
	private int level;

	public HeaderFactory(int level) {
		this.tag = tagTemplate + String.valueOf(level);
		this.level = level;
	}

	public String getTag() {
		return tag;
	}

	public AbstractParserPlugin getPluginParser() {
		return new HeaderElement(level, tag);
	}

	public EditorButtonResources getEditorButtonResources() {
		EditorButtonResources resources = new EditorButtonResources(tagTemplate
				+ level);
		resources.setTagFamily("Nadpisy");
		return resources;
	}

}
