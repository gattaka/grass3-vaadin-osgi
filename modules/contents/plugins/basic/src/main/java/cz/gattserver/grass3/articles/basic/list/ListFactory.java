package cz.gattserver.grass3.articles.basic.list;

import cz.gattserver.grass3.articles.editor.api.EditorButtonResources;
import cz.gattserver.grass3.articles.parser.interfaces.AbstractParserPlugin;
import cz.gattserver.grass3.articles.parser.interfaces.IPluginFactory;


/**
 * 
 * @author gatt
 */
public class ListFactory implements IPluginFactory {

	private final String unorderedTag = "UL";
	private final String unorderedImage = "articles/basic/img/ul_16.png";
	private final String orderedTag = "OL";
	private final String orderedImage = "articles/basic/img/ol_16.png";

	private boolean ordered;
	
	private String tag;
	private String image;

	public String getTag() {
		return tag;
	}

	public AbstractParserPlugin getPluginParser() {
		return new ListElement(tag, ordered);
	}

	public ListFactory(boolean ordered) {
		this.ordered = ordered;
		if (ordered) {
			tag = orderedTag;
			image = orderedImage;
		} else {
			tag = unorderedTag;
			image = unorderedImage;
		}

	}

	public EditorButtonResources getEditorButtonResources() {
		EditorButtonResources resources = new EditorButtonResources(tag);
		resources.setImageName(image);
		resources.setDescription("");
		resources.setTagFamily("HTML");
		return resources;
	}
}
