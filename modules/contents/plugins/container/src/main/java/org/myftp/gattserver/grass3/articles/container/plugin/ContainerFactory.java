package org.myftp.gattserver.grass3.articles.container.plugin;

import org.myftp.gattserver.grass3.articles.editor.api.EditorButtonResources;
import org.myftp.gattserver.grass3.articles.parser.interfaces.AbstractParserPlugin;
import org.myftp.gattserver.grass3.articles.parser.interfaces.IPluginFactory;
import org.myftp.gattserver.grass3.articles.container.plugin.ContainerElement;

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

	public EditorButtonResources getEditorButtonResources() {
		EditorButtonResources resources = new EditorButtonResources(tag);
		resources.setTagFamily("Kontejner");
		return resources;
	}
}
