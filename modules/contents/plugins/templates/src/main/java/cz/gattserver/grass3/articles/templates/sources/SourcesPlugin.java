package cz.gattserver.grass3.articles.templates.sources;

import org.springframework.stereotype.Component;

import cz.gattserver.grass3.articles.editor.parser.Parser;
import cz.gattserver.grass3.articles.editor.parser.interfaces.EditorButtonResourcesTO;
import cz.gattserver.grass3.articles.plugins.Plugin;

/**
 * @author gatt
 */
@Component
public class SourcesPlugin implements Plugin {

	private final String tag = "SOURCES";

	@Override
	public String getTag() {
		return tag;
	}

	@Override
	public Parser getParser() {
		return new SourcesParser(tag);
	}

	@Override
	public EditorButtonResourcesTO getEditorButtonResources() {
		EditorButtonResourcesTO resources = new EditorButtonResourcesTO(tag);
		resources.setDescription("Zdroje");
		resources.setPrefix("[" + tag + "]");
		resources.setSuffix("[/" + tag + "]");
		resources.setTagFamily("Šablony");
		return resources;
	}
}
