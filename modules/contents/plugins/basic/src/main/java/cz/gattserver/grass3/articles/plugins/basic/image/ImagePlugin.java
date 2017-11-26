package cz.gattserver.grass3.articles.plugins.basic.image;

import org.springframework.stereotype.Component;

import cz.gattserver.grass3.articles.editor.parser.Parser;
import cz.gattserver.grass3.articles.editor.parser.interfaces.EditorButtonResourcesTO;
import cz.gattserver.grass3.articles.editor.parser.interfaces.EditorButtonResourcesTOBuilder;
import cz.gattserver.grass3.articles.plugins.Plugin;
import cz.gattserver.web.common.ui.ImageIcons;

/**
 * @author gatt
 */
@Component
public class ImagePlugin implements Plugin {

	private final String tag = "IMG";
	private final String description = "Obrázek";
	private final String image = ImageIcons.IMG_16_ICON;

	@Override
	public String getTag() {
		return tag;
	}

	@Override
	public Parser getParser() {
		return new ImageParser(tag);
	}

	@Override
	public EditorButtonResourcesTO getEditorButtonResources() {
		return new EditorButtonResourcesTOBuilder(tag, "HTML").setDescription(description)
				.setImageAsThemeResource(image).build();
	}
}
