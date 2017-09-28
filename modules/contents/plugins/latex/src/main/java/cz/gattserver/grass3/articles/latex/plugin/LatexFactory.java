package cz.gattserver.grass3.articles.latex.plugin;

import cz.gattserver.grass3.articles.editor.api.EditorButtonResources;
import cz.gattserver.grass3.articles.parser.interfaces.AbstractParserPlugin;
import cz.gattserver.grass3.articles.parser.interfaces.PluginFactory;

/**
 * 
 * @author gatt
 */
public class LatexFactory implements PluginFactory {

	private final String tag = "TEX";

	public String getTag() {
		return tag;
	}

	public AbstractParserPlugin getPluginParser() {
		return new LatexElement(tag);
	}

	public EditorButtonResources getEditorButtonResources() {
		EditorButtonResources resources = new EditorButtonResources(tag);
		resources.setTagFamily("LaTeX");
		return resources;
	}

}
