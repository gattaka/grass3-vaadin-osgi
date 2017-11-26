package cz.gattserver.grass3.articles.plugins.basic.list;

import cz.gattserver.grass3.articles.editor.parser.Parser;
import cz.gattserver.grass3.articles.editor.parser.interfaces.EditorButtonResourcesTO;
import cz.gattserver.grass3.articles.editor.parser.interfaces.EditorButtonResourcesTOBuilder;
import cz.gattserver.grass3.articles.plugins.Plugin;

/**
 * @author gatt
 */
public abstract class AbstractListPlugin implements Plugin {

	private boolean ordered;

	private String tag;
	private String image;

	public AbstractListPlugin(String tag, String image) {
		this.tag = tag;
		this.image = image;
	}

	@Override
	public String getTag() {
		return tag;
	}

	@Override
	public Parser getParser() {
		return new ListParser(tag, ordered);
	}

	@Override
	public EditorButtonResourcesTO getEditorButtonResources() {
		return new EditorButtonResourcesTOBuilder(tag, "HTML").setImageAsThemeResource(image).build();
	}
}
