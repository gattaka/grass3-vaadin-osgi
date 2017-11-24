package cz.gattserver.grass3.articles.plugins.basic.list;

import org.springframework.stereotype.Component;

import cz.gattserver.grass3.articles.editor.parser.Parser;
import cz.gattserver.grass3.articles.editor.parser.interfaces.EditorButtonResourcesTO;
import cz.gattserver.grass3.articles.plugins.Plugin;

/**
 * @author gatt
 */
@Component
public class ListPlugin implements Plugin {

	private final String unorderedTag = "UL";
	private final String unorderedImage = "articles/basic/img/ul_16.png";
	private final String orderedTag = "OL";
	private final String orderedImage = "articles/basic/img/ol_16.png";

	private boolean ordered;

	private String tag;
	private String image;

	public ListPlugin(boolean ordered) {
		this.ordered = ordered;
		if (ordered) {
			tag = orderedTag;
			image = orderedImage;
		} else {
			tag = unorderedTag;
			image = unorderedImage;
		}

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
		EditorButtonResourcesTO resources = new EditorButtonResourcesTO(tag);
		resources.setImageName(image);
		resources.setDescription("");
		resources.setTagFamily("HTML");
		return resources;
	}
}
