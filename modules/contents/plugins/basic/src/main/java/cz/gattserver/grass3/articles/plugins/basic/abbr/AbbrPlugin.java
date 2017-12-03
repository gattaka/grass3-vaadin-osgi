package cz.gattserver.grass3.articles.plugins.basic.abbr;

import org.springframework.stereotype.Component;

import cz.gattserver.grass3.articles.editor.parser.Parser;
import cz.gattserver.grass3.articles.editor.parser.interfaces.EditorButtonResourcesTO;
import cz.gattserver.grass3.articles.editor.parser.interfaces.EditorButtonResourcesTOBuilder;
import cz.gattserver.grass3.articles.plugins.Plugin;

/**
 * @author gatt
 */
@Component
public class AbbrPlugin implements Plugin {

	private static final String TAG = "ABBR";
	private static final String TITLE_TAG = "T";
	private static final String IMAGE_PATH = "articles/basic/img/abbr_16.png";

	@Override
	public String getTag() {
		return TAG;
	}

	@Override
	public Parser getParser() {
		return new AbbrParser(TAG, TITLE_TAG);
	}

	@Override
	public EditorButtonResourcesTO getEditorButtonResources() {
		return new EditorButtonResourcesTOBuilder(TAG, "HTML").setPrefix("[" + TAG + "]")
				.setSuffix("[" + TITLE_TAG + "][/" + TITLE_TAG + "][/" + TAG + "]").setImageAsThemeResource(IMAGE_PATH)
				.build();
	}
}
