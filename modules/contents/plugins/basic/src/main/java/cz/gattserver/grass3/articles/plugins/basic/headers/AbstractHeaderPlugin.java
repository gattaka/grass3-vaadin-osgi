package cz.gattserver.grass3.articles.plugins.basic.headers;

import cz.gattserver.grass3.articles.editor.parser.Parser;
import cz.gattserver.grass3.articles.editor.parser.interfaces.EditorButtonResourcesTO;
import cz.gattserver.grass3.articles.plugins.Plugin;

/**
 * @author gatt
 */
public abstract class AbstractHeaderPlugin implements Plugin {

	private String tagTemplate = "N";
	private String tag;
	private int level;

	public AbstractHeaderPlugin(int level) {
		this.tag = tagTemplate + String.valueOf(level);
		this.level = level;
	}

	@Override
	public String getTag() {
		return tag;
	}

	@Override
	public Parser getParser() {
		return new HeaderParser(level, tag);
	}

	@Override
	public EditorButtonResourcesTO getEditorButtonResources() {
		EditorButtonResourcesTO resources = new EditorButtonResourcesTO(tagTemplate + level);
		resources.setTagFamily("Nadpisy");
		return resources;
	}

}
