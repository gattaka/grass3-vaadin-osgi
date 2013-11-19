package org.myftp.gattserver.grass3.articles.code;

import org.myftp.gattserver.grass3.articles.editor.api.EditorButtonResources;
import org.myftp.gattserver.grass3.articles.parser.interfaces.AbstractParserPlugin;
import org.myftp.gattserver.grass3.articles.parser.interfaces.IPluginFactory;

/**
 * 
 * @author gatt
 */
public class CodeFactory implements IPluginFactory {

	private String tag;
	private String style;
	private String description;
	private String image;

	private HighlightEngine highlightEngine = HighlightEngine.SHJS;

	public CodeFactory(String tag, String style) {
		this(tag, style, tag, "");
	}

	public CodeFactory(String tag, String style, String description,
			String image) {
		this.style = style;
		this.tag = tag;
		this.description = description;
		this.image = image;
	}

	public String getTag() {
		return tag;
	}

	public AbstractParserPlugin getPluginParser() {
		return new CodeElement(tag, style, description, highlightEngine);
	}

	public HighlightEngine getHighlightEngine() {
		return highlightEngine;
	}

	public void setHighlightEngine(HighlightEngine highlightEngine) {
		this.highlightEngine = highlightEngine;
	}

	public EditorButtonResources getEditorButtonResources() {
		EditorButtonResources resources = new EditorButtonResources(tag);
		if (image != null && !image.isEmpty())
			resources.setImageName("articles/code/img/" + image);
		resources.setDescription(description);
		resources.setTagFamily("Code highlight");
		return resources;
	}
}
