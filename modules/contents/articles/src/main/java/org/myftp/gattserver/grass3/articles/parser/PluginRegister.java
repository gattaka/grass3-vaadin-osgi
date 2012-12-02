package org.myftp.gattserver.grass3.articles.parser;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.myftp.gattserver.grass3.articles.parser.interfaces.IPluginFactory;

/**
 * 
 * @author gatt
 */
public enum PluginRegister {

	INSTANCE;

	private Map<String, IPluginFactory> plugins = new HashMap<String, IPluginFactory>();

	/**
	 * Registers tag for parser and insert tags in editor tag catalog
	 * 
	 * @param pluginFactory
	 *            factory of the plugin to get its instances
	 * @return true if the element registration was successful, false if the tag
	 *         key is occupied
	 */
	public boolean registerPlugin(IPluginFactory pluginFactory) {

		if (plugins.containsKey(pluginFactory.getTag())) {
			return false;
		}

		// parser register
		plugins.put(pluginFactory.getTag(), pluginFactory);

		return true;

	}

	public Set<String> getRegisteredTags() {
		return plugins.keySet();
	}

	public boolean isRegistered(String tag) {
		return plugins.containsKey(tag);
	}

	public IPluginFactory get(String tag) {

		// je tady takový plugin ?
		if (!isRegistered(tag)) {
			return null;
		}

		return plugins.get(tag);

	}

	public IPluginFactory unregisterPlugin(String tag) {
		return plugins.remove(tag);
	}
}
