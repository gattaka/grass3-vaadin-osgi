package cz.gattserver.grass3.articles.plugins.basic.abbr;

import org.springframework.stereotype.Component;

import cz.gattserver.grass3.articles.editor.parser.Parser;
import cz.gattserver.grass3.articles.editor.parser.interfaces.EditorButtonResourcesTO;
import cz.gattserver.grass3.articles.plugins.Plugin;

/**
 * @author gatt
 */
@Component
public class AbbrPlugin implements Plugin {

	private final String tag = "ABBR";
	private final String titleTag = "T";
	private String image = "articles/basic/img/abbr_16.png";

	@Override
	public String getTag() {
		return tag;
	}

	@Override
	public Parser getParser() {
		return new AbbrParser(tag, titleTag);
	}

	@Override
	public EditorButtonResourcesTO getEditorButtonResources() {
		EditorButtonResourcesTO resources = new EditorButtonResourcesTO(tag);
		resources.setImageName(image);
		resources.setDescription("");
		resources.setPrefix("[" + tag + "]");
		resources.setSuffix("[" + titleTag + "][/" + titleTag + "][/" + tag + "]");
		resources.setTagFamily("HTML");
		return resources;
	}
}
