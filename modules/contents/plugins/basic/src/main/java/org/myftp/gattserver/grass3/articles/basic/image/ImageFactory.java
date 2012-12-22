package org.myftp.gattserver.grass3.articles.basic.image;

import org.myftp.gattserver.grass3.articles.editor.api.EditorButtonResources;
import org.myftp.gattserver.grass3.articles.parser.interfaces.AbstractParserPlugin;
import org.myftp.gattserver.grass3.articles.parser.interfaces.IPluginFactory;


/**
 * 
 * @author gatt
 */
public class ImageFactory implements IPluginFactory {

	private final String tag = "IMG";
	private final String description = "Obrázek";
	private final String image = "/grass/img/tags/img_16.png";

	public String getTag() {
		return tag;
	}

	public AbstractParserPlugin getPluginParser() {
		return new ImageElement(tag);
	}

	public EditorButtonResources getEditorButtonResources() {
		return new EditorButtonResources(description, '[' + tag + ']', "[/" + tag + ']', image);
	}

	public String getTagFamily() {
		return "HTML";
	}
}
