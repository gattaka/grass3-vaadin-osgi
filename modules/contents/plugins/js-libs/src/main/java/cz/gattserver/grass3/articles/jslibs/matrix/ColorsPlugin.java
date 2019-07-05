package cz.gattserver.grass3.articles.jslibs.matrix;

import org.springframework.stereotype.Component;

import cz.gattserver.grass3.articles.editor.parser.Parser;
import cz.gattserver.grass3.articles.editor.parser.interfaces.EditorButtonResourcesTO;
import cz.gattserver.grass3.articles.editor.parser.interfaces.EditorButtonResourcesTOBuilder;
import cz.gattserver.grass3.articles.jslibs.GJSLibParser;
import cz.gattserver.grass3.articles.plugins.Plugin;

/**
 * @author gatt
 */
@Component
public class ColorsPlugin implements Plugin {

	private static final String TAG = "GJSLibColors";
	private static final String IMAGE_PATH = "articles/jslibs/img/colors_16.png";

	@Override
	public String getTag() {
		return TAG;
	}

	@Override
	public Parser getParser() {
		return new GJSLibParser(TAG);
	}

	@Override
	public EditorButtonResourcesTO getEditorButtonResources() {
		return new EditorButtonResourcesTOBuilder(TAG, "JS Libs").setPrefix("[" + TAG + "]").setSuffix("[/" + TAG + "]")
				.setDescription("Colors").setImageAsThemeResource(IMAGE_PATH).build();
	}
}
