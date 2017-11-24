package cz.gattserver.grass3.articles.plugins.register.impl;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cz.gattserver.grass3.articles.editor.parser.interfaces.EditorButtonResourcesTO;
import cz.gattserver.grass3.articles.plugins.Plugin;
import cz.gattserver.grass3.articles.plugins.register.PluginRegister;

/**
 * @author gatt
 */
@Component
public class PluginRegisterImpl implements PluginRegister {

	@Autowired(required = false)
	private List<Plugin> injectedPlugins;

	/**
	 * Pluginy dle skupin
	 */
	private Map<String, Map<String, EditorButtonResourcesTO>> editorCatalog = new HashMap<String, Map<String, EditorButtonResourcesTO>>();
	private Map<String, Plugin> plugins = new HashMap<String, Plugin>();

	@PostConstruct
	private void init() {
		if (injectedPlugins == null)
			return;
		for (Plugin plugin : injectedPlugins) {
			registerPlugin(plugin);
			addButtonToGroup(plugin.getEditorButtonResources());
		}
	}

	@Override
	public boolean registerPlugin(Plugin plugin) {
		if (plugins.containsKey(plugin.getTag())) {
			return false;
		}
		plugins.put(plugin.getTag(), plugin);
		return true;
	}

	@Override
	public Set<String> getRegisteredTags() {
		return plugins.keySet();
	}

	@Override
	public boolean isRegistered(String tag) {
		return plugins.containsKey(tag);
	}

	@Override
	public Plugin get(String tag) {
		if (!isRegistered(tag)) {
			return null;
		}
		return plugins.get(tag);
	}

	@Override
	public Plugin unregisterPlugin(String tag) {
		return plugins.remove(tag);
	}

	@Override
	public synchronized Set<String> getRegisteredGroups() {
		return new HashSet<String>(editorCatalog.keySet());
	}

	@Override
	public synchronized Set<EditorButtonResourcesTO> getGroupTags(String group) {
		return new HashSet<EditorButtonResourcesTO>(editorCatalog.get(group).values());
	}

	private void addButtonToGroup(EditorButtonResourcesTO resources) {
		// existuje skupina ?
		if (editorCatalog.containsKey(resources.getTagFamily())) {
			editorCatalog.get(resources.getTagFamily()).put(resources.getTag(), resources);
		} else {
			// založ
			Map<String, EditorButtonResourcesTO> map = new HashMap<String, EditorButtonResourcesTO>();
			map.put(resources.getTag(), resources);
			editorCatalog.put(resources.getTagFamily(), map);
		}
	}
}
