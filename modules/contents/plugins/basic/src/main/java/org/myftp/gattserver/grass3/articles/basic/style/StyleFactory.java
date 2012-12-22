package org.myftp.gattserver.grass3.articles.basic.style;

import org.myftp.gattserver.grass3.articles.parser.interfaces.IPluginFactory;

/**
 * 
 * @author gatt
 */
public abstract class StyleFactory implements IPluginFactory {

	private final String tag;

	public StyleFactory(String tag) {
		this.tag = tag;
	}

	public String getTag() {
		return tag;
	}

	public String getTagFamily() {
		return "Formátování";
	}
}
