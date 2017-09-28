package cz.gattserver.grass3.articles.container.plugin;

import cz.gattserver.grass3.articles.container.plugin.ContainerElement;
import cz.gattserver.grass3.articles.editor.api.EditorButtonResources;
import cz.gattserver.grass3.articles.parser.interfaces.AbstractParserPlugin;
import cz.gattserver.grass3.articles.parser.interfaces.PluginFactory;

/**
 * 
 * @author gatt
 */
public class ContainerFactory implements PluginFactory {

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
