package cz.gattserver.grass3.articles.templates.sources;

import org.springframework.stereotype.Component;

import cz.gattserver.grass3.articles.editor.parser.Parser;
import cz.gattserver.grass3.articles.editor.parser.interfaces.EditorButtonResourcesTO;
import cz.gattserver.grass3.articles.editor.parser.interfaces.EditorButtonResourcesTOBuilder;
import cz.gattserver.grass3.articles.plugins.Plugin;

/**
 * @author gatt
 */
@Component
public class DictionaryPlugin implements Plugin {

	private final String tag = "DICTIONARY";

	@Override
	public String getTag() {
		return tag;
	}

	@Override
	public Parser getParser() {
		return new DictionaryParser(tag);
	}

	@Override
	public EditorButtonResourcesTO getEditorButtonResources() {
		return new EditorButtonResourcesTOBuilder(tag, "Šablony").setDescription("Slovník").build();
	}
}
