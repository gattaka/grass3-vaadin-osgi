package cz.gattserver.grass3.articles.templates.sources;

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

	@Override
	public String getTag() {
		return TAG;
	}

	@Override
	public Parser getParser() {
		return new SourcesParser(TAG, new CombinedFaviconObtainStrategy());
	}

	@Override
	public EditorButtonResourcesTO getEditorButtonResources() {
		return new EditorButtonResourcesTOBuilder(TAG, "Šablony").setDescription("Zdroje").build();
	}
}
