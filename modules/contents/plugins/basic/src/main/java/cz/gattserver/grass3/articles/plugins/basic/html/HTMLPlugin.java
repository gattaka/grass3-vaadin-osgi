package cz.gattserver.grass3.articles.plugins.basic.html;

import org.springframework.stereotype.Component;

import cz.gattserver.grass3.articles.editor.parser.Parser;
import cz.gattserver.grass3.articles.editor.parser.interfaces.EditorButtonResourcesTO;
import cz.gattserver.grass3.articles.editor.parser.interfaces.EditorButtonResourcesTOBuilder;
import cz.gattserver.grass3.articles.plugins.Plugin;

/**
 * @author gatt
 */
@Component
public class HTMLPlugin implements Plugin {

	private static final String TAG = "HTML";
	private static final String DESCRIPTION = "HTML";
	private static final String IMAGE_PATH = "articles/basic/img/htmlxml_16.png";

	@Override
	public String getTag() {
		return TAG;
	}

	@Override
	public Parser getParser() {
		return new HTMLParser(TAG);
	}

	@Override
	public EditorButtonResourcesTO getEditorButtonResources() {
		return new EditorButtonResourcesTOBuilder(TAG, "HTML").setDescription(DESCRIPTION)
				.setImageAsThemeResource(IMAGE_PATH).build();
	}
}
