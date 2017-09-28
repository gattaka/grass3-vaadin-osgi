package cz.gattserver.grass3.articles.favlink.plugin;

import cz.gattserver.grass3.articles.editor.api.EditorButtonResources;
import cz.gattserver.grass3.articles.parser.interfaces.AbstractParserPlugin;
import cz.gattserver.grass3.articles.parser.interfaces.PluginFactory;
import cz.gattserver.web.common.ui.ImageIcons;

/**
 * 
 * @author gatt
 */
public class LinkFactory implements PluginFactory {

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
				.setImageName(ImageIcons.GLOBE_16_ICON);
		resources.setDescription("Link");
		resources.setTagFamily("HTML");
		return resources;
	}
}
