package cz.gattserver.grass3.articles.favlink.plugin;

import org.springframework.stereotype.Component;

import cz.gattserver.grass3.articles.editor.parser.Parser;
import cz.gattserver.grass3.articles.editor.parser.interfaces.EditorButtonResourcesTO;
import cz.gattserver.grass3.articles.plugins.Plugin;
import cz.gattserver.web.common.ui.ImageIcons;

/**
 * @author gatt
 */
@Component
public class LinkPlugin implements Plugin {

	private final String tag = "A";

	@Override
	public String getTag() {
		return tag;
	}

	@Override
	public Parser getParser() {
		return new LinkParser(tag);
	}

	@Override
	public EditorButtonResourcesTO getEditorButtonResources() {
		EditorButtonResourcesTO resources = new EditorButtonResourcesTO(tag);
		resources.setImageName(ImageIcons.GLOBE_16_ICON);
		resources.setDescription("Link");
		resources.setTagFamily("HTML");
		return resources;
	}
}
