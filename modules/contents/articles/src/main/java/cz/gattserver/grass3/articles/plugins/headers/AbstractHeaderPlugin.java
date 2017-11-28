package cz.gattserver.grass3.articles.plugins.headers;

import cz.gattserver.grass3.articles.editor.parser.Parser;
import cz.gattserver.grass3.articles.editor.parser.interfaces.EditorButtonResourcesTO;
import cz.gattserver.grass3.articles.editor.parser.interfaces.EditorButtonResourcesTOBuilder;
import cz.gattserver.grass3.articles.plugins.Plugin;
import cz.gattserver.grass3.articles.plugins.headers.HeaderParser;

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
		return new EditorButtonResourcesTOBuilder(tagTemplate + level, "Nadpisy").build();
	}

}