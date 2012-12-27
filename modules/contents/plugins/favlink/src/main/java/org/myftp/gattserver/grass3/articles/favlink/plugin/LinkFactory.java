package org.myftp.gattserver.grass3.articles.favlink.plugin;

import org.myftp.gattserver.grass3.articles.editor.api.EditorButtonResources;
import org.myftp.gattserver.grass3.articles.parser.interfaces.AbstractParserPlugin;
import org.myftp.gattserver.grass3.articles.parser.interfaces.IPluginFactory;

/**
 * 
 * @author gatt
 */
public class LinkFactory implements IPluginFactory {

	private final String tag = "A";

	public String getTag() {
		return tag;
	}

	public AbstractParserPlugin getPluginParser() {
		return new LinkElement(tag);
	}

	public EditorButtonResources getEditorButtonResources() {
		EditorButtonResources resources = new EditorButtonResources(tag);
		resources
				.setImageName("img/tags/globe_16.png");
		resources.setDescription("Link");
		resources.setTagFamily("HTML");
		return resources;
	}
}
