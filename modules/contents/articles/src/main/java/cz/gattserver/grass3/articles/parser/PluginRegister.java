package cz.gattserver.grass3.articles.parser;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Component;

import cz.gattserver.grass3.articles.parser.interfaces.PluginFactory;

/**
 * 
 * @author gatt
 */
@Component("pluginRegister")
public class PluginRegister {

	private Map<String, PluginFactory> plugins = new HashMap<String, PluginFactory>();

	/**
	 * Registers tag for parser and insert tags in editor tag catalog
	 * 
	 * @param pluginFactory
	 *            factory of the plugin to get its instances
	 * @return true if the element registration was successful, false if the tag
	 *         key is occupied
	 */
	public boolean registerPlugin(PluginFactory pluginFactory) {

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

	public PluginFactory get(String tag) {

		// je tady takový plugin ?
		if (!isRegistered(tag)) {
			return null;
		}

		return plugins.get(tag);

	}

	public PluginFactory unregisterPlugin(String tag) {
		return plugins.remove(tag);
	}
}
