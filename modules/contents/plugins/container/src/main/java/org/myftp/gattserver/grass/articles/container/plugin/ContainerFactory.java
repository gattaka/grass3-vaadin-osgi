package org.myftp.gattserver.grass.articles.container.plugin;

import org.myftp.gattserver.grass.articles.container.plugin.ContainerElement;
import org.myftp.gattserver.grass.articles.editor.api.EditorButtonResources;
import org.myftp.gattserver.grass.articles.parser.interfaces.AbstractParserPlugin;
import org.myftp.gattserver.grass.articles.parser.interfaces.IPluginFactory;


/**
 * 
 * @author gatt
 */
public class ContainerFactory implements IPluginFactory {

	private final String tag = "CONT";

	public String getTag() {
		return tag;
	}

	public AbstractParserPlugin getPluginParser() {
		return new ContainerElement(tag);
	}

	public String getTagFamily() {
		return "Kontejner";
	}

	public EditorButtonResources getEditorButtonResources() {
		return new EditorButtonResources(tag);
	}
}
