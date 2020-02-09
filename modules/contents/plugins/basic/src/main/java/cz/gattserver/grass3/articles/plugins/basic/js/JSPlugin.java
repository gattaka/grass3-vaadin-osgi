package cz.gattserver.grass3.articles.plugins.basic.js;

import org.springframework.stereotype.Component;

import cz.gattserver.grass3.articles.editor.parser.Parser;
import cz.gattserver.grass3.articles.editor.parser.interfaces.EditorButtonResourcesTO;
import cz.gattserver.grass3.articles.editor.parser.interfaces.EditorButtonResourcesTOBuilder;
import cz.gattserver.grass3.articles.plugins.Plugin;

/**
 * @author gatt
 */
@Component
public class JSPlugin implements Plugin {

	private static final String TAG = "JSSOURCE";
	private static final String DESCRIPTION = "JavaScript";

	@Override
	public String getTag() {
		return TAG;
	}

	@Override
	public Parser getParser() {
		return new JSParser(TAG);
	}

	@Override
	public EditorButtonResourcesTO getEditorButtonResources() {
		return new EditorButtonResourcesTOBuilder(TAG, "HTML").setDescription(DESCRIPTION)
				.setImageAsThemeResource("basic/img/js_16.png").build();
	}
}
