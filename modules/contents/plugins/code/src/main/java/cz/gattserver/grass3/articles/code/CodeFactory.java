package cz.gattserver.grass3.articles.code;

import cz.gattserver.grass3.articles.editor.api.EditorButtonResources;
import cz.gattserver.grass3.articles.parser.interfaces.AbstractParserPlugin;
import cz.gattserver.grass3.articles.parser.interfaces.IPluginFactory;

/**
 * 
 * @author gatt
 */
public class CodeFactory implements IPluginFactory {

	private String tag;
	private String description;
	private String image;
	private String lib;
	private String mode;

	public CodeFactory(String tag, String description, String image, String lib, String mode) {
		this.tag = tag;
		this.description = description;
		this.image = image;
		this.lib = lib;
		this.mode = mode;
	}

	public String getTag() {
		return tag;
	}

	public AbstractParserPlugin getPluginParser() {
		return new CodeElement(tag, description, lib, mode);
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
