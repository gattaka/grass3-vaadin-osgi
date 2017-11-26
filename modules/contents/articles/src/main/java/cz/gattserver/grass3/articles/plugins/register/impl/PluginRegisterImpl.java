package cz.gattserver.grass3.articles.plugins.register.impl;

import java.util.ArrayList;
import java.util.Collections;
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
	private Map<String, Map<String, EditorButtonResourcesTO>> editorCatalog;
	private Map<String, Plugin> plugins;

	@PostConstruct
	private void init() {
		// Ošetření null kolekcí
		if (injectedPlugins == null)
			injectedPlugins = new ArrayList<>();

		editorCatalog = new HashMap<String, Map<String, EditorButtonResourcesTO>>();
		plugins = new HashMap<String, Plugin>();
		for (Plugin plugin : injectedPlugins) {
			registerPlugin(plugin);
			addButtonToGroup(plugin.getEditorButtonResources());
		}
	}

	private Plugin registerPlugin(Plugin plugin) {
		return plugins.put(plugin.getTag(), plugin);
	}

	@Override
	public Set<String> getRegisteredTags() {
		return Collections.unmodifiableSet(plugins.keySet());
	}

	@Override
	public boolean isRegistered(String tag) {
		return plugins.containsKey(tag);
	}

	@Override
	public Plugin get(String tag) {
		return plugins.get(tag);
	}

	@Override
	public Set<String> getRegisteredGroups() {
		return Collections.unmodifiableSet(editorCatalog.keySet());
	}

	@Override
	public Set<EditorButtonResourcesTO> getGroupTags(String group) {
		Map<String, EditorButtonResourcesTO> resources = editorCatalog.get(group);
		if (resources == null)
			return new HashSet<EditorButtonResourcesTO>();
		else
			return new HashSet<EditorButtonResourcesTO>(resources.values());
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
