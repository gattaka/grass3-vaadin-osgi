package cz.gattserver.grass3.articles.templates.container;

import org.springframework.stereotype.Component;

import cz.gattserver.grass3.articles.editor.parser.Parser;
import cz.gattserver.grass3.articles.editor.parser.interfaces.EditorButtonResourcesTO;
import cz.gattserver.grass3.articles.editor.parser.interfaces.EditorButtonResourcesTOBuilder;
import cz.gattserver.grass3.articles.plugins.Plugin;
import cz.gattserver.grass3.articles.plugins.favlink.strategies.CombinedFaviconObtainStrategy;

/**
 * @author gatt
 */
@Component
public class SourcesPlugin implements Plugin {

	private static final String TAG = "SOURCES";
	private static final String IMAGE_PATH = "templates/img/sources_16.png";

	@Override
	public String getTag() {
		return TAG;
	}

	@Override
	public Parser getParser() {
		return new SourcesParser(TAG, new CombinedFaviconObtainStrategy(), true, true);
	}

	@Override
	public EditorButtonResourcesTO getEditorButtonResources() {
		return new EditorButtonResourcesTOBuilder(TAG, "Šablony").setDescription("Zdroje")
				.setImageAsThemeResource(IMAGE_PATH).build();
	}
}
