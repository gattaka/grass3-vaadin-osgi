package cz.gattserver.grass3.articles.basic.style;

import cz.gattserver.grass3.articles.parser.interfaces.PluginFactory;

/**
 * 
 * @author gatt
 */
public abstract class StyleFactory implements PluginFactory {

	private final String tag;

	public StyleFactory(String tag) {
		this.tag = tag;
	}

	public String getTag() {
		return tag;
	}
}
