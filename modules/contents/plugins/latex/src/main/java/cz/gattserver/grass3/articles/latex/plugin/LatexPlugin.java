package cz.gattserver.grass3.articles.latex.plugin;

import org.springframework.stereotype.Component;

import cz.gattserver.grass3.articles.editor.parser.Parser;
import cz.gattserver.grass3.articles.editor.parser.interfaces.EditorButtonResourcesTO;
import cz.gattserver.grass3.articles.editor.parser.interfaces.EditorButtonResourcesTOBuilder;
import cz.gattserver.grass3.articles.plugins.Plugin;

/**
 * @author gatt
 */
@Component
public class LatexPlugin implements Plugin {

	private final String tag = "TEX";

	@Override
	public String getTag() {
		return tag;
	}

	@Override
	public Parser getParser() {
		return new LatexParser(tag);
	}

	@Override
	public EditorButtonResourcesTO getEditorButtonResources() {
		return new EditorButtonResourcesTOBuilder(tag, "LaTeX").build();
	}

}
