package cz.gattserver.grass3.articles.code;

import org.apache.commons.lang3.StringUtils;

import cz.gattserver.grass3.articles.editor.parser.Parser;
import cz.gattserver.grass3.articles.editor.parser.interfaces.EditorButtonResourcesTO;
import cz.gattserver.grass3.articles.editor.parser.interfaces.EditorButtonResourcesTOBuilder;
import cz.gattserver.grass3.articles.plugins.Plugin;

/**
 * @author gatt
 */
public abstract class AbstractCodePlugin implements Plugin {

	private String tag;
	private String description;
	private String image;
	private String lib;
	private String mode;

	public AbstractCodePlugin(String tag, String description, String image, String lib, String mode) {
		this.tag = tag;
		this.description = description;
		this.image = image;
		this.lib = lib;
		this.mode = mode;
	}

	@Override
	public String getTag() {
		return tag;
	}

	@Override
	public Parser getParser() {
		return new CodeParser(tag, description, lib, mode);
	}

	@Override
	public EditorButtonResourcesTO getEditorButtonResources() {
		EditorButtonResourcesTOBuilder builder = new EditorButtonResourcesTOBuilder(tag, "Code highlight")
				.setDescription(description);
		if (StringUtils.isNotBlank(image))
			builder.setImageAsThemeResource("articles/code/img/" + image);
		return builder.build();
	}
}
