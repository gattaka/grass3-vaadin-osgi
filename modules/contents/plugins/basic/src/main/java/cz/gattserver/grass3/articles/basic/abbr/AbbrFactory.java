package cz.gattserver.grass3.articles.basic.abbr;

import cz.gattserver.grass3.articles.editor.api.EditorButtonResources;
import cz.gattserver.grass3.articles.parser.interfaces.AbstractParserPlugin;
import cz.gattserver.grass3.articles.parser.interfaces.IPluginFactory;

/**
 * 
 * @author gatt
 */
public class AbbrFactory implements IPluginFactory {

	private final String tag = "ABBR";
	private final String titleTag = "T";
	private String image = "articles/basic/img/abbr_16.png";

	public String getTag() {
		return tag;
	}

	public AbstractParserPlugin getPluginParser() {
		return new AbbrElement(tag, titleTag);
	}

	public AbbrFactory() {
	}

	public EditorButtonResources getEditorButtonResources() {
		EditorButtonResources resources = new EditorButtonResources(tag);
		resources.setImageName(image);
		resources.setDescription("");
		resources.setPrefix("[" + tag + "]");
		resources.setSuffix("[" + titleTag + "][/" + titleTag + "][/" + tag
				+ "]");
		resources.setTagFamily("HTML");
		return resources;
	}
}
