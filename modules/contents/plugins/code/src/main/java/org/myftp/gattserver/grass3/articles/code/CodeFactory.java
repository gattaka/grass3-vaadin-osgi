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
	private String description;
	private String image;
	private String style;
	private String[] libs;

	public CodeFactory(String tag, String description, String image,
			String style, String... libs) {
		this.tag = tag;
		this.description = description;
		this.image = image;
		this.style = style;
		this.libs = libs;
	}

	public String getTag() {
		return tag;
	}

	public AbstractParserPlugin getPluginParser() {
		return new CodeElement(tag, description, style, libs);
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
