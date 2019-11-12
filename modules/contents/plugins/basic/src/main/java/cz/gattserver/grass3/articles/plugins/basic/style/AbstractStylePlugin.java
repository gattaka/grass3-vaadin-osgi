package cz.gattserver.grass3.articles.plugins.basic.style;

import cz.gattserver.grass3.articles.plugins.Plugin;

/**
 * @author gatt
 */
public abstract class AbstractStylePlugin implements Plugin {

	public static final String PLUGIN_FAMILY = "Formátování";
	
	private final String tag;

	public AbstractStylePlugin(String tag) {
		this.tag = tag;
	}

	@Override
	public String getTag() {
		return tag;
	}
}
