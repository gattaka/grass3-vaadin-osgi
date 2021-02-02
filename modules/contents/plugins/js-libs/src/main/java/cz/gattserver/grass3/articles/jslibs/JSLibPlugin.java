package cz.gattserver.grass3.articles.jslibs;

import org.springframework.stereotype.Component;

import cz.gattserver.grass3.articles.editor.parser.Parser;
import cz.gattserver.grass3.articles.editor.parser.interfaces.EditorButtonResourcesTO;
import cz.gattserver.grass3.articles.editor.parser.interfaces.EditorButtonResourcesTOBuilder;
import cz.gattserver.grass3.articles.plugins.Plugin;

/**
 * @author gatt
 */
@Component
public class JSLibPlugin implements Plugin {

	private static final String TAG = "JSLib";
	private static final String IMAGE_PATH = "jslibs/img/js.ico";

	@Override
	public String getTag() {
		return TAG;
	}

	@Override
	public Parser getParser() {
		return new JSLibParser(TAG);
	}

	@Override
	public EditorButtonResourcesTO getEditorButtonResources() {
		return new EditorButtonResourcesTOBuilder(TAG, "JS Libs").setPrefix("[" + TAG + "]").setSuffix("[/" + TAG + "]")
				.setDescription("JS lib").setImageAsThemeResource(IMAGE_PATH).build();
	}
}
