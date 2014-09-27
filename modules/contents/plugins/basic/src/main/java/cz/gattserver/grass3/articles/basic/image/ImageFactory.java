package cz.gattserver.grass3.articles.basic.image;

import cz.gattserver.grass3.articles.editor.api.EditorButtonResources;
import cz.gattserver.grass3.articles.parser.interfaces.AbstractParserPlugin;
import cz.gattserver.grass3.articles.parser.interfaces.IPluginFactory;

/**
 * 
 * @author gatt
 */
public class ImageFactory implements IPluginFactory {

	private final String tag = "IMG";
	private final String description = "Obrázek";
	private final String image = "img/tags/img_16.png";

	public String getTag() {
		return tag;
	}

	public AbstractParserPlugin getPluginParser() {
		return new ImageElement(tag);
	}

	public EditorButtonResources getEditorButtonResources() {
		EditorButtonResources resources = new EditorButtonResources(tag,
				description, '[' + tag + ']', "[/" + tag + ']', image);
		resources.setTagFamily("HTML");
		return resources;
	}
}
