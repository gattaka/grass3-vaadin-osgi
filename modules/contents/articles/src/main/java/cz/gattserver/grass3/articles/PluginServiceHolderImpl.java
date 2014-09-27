package cz.gattserver.grass3.articles;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cz.gattserver.grass3.articles.editor.api.EditorButtonResources;
import cz.gattserver.grass3.articles.parser.PluginRegister;
import cz.gattserver.grass3.articles.service.IPluginService;

@Component("pluginServiceHolder")
public class PluginServiceHolderImpl implements IPluginServiceHolder {

	@Resource(name = "pluginRegister")
	private PluginRegister pluginRegister;

	@Autowired(required = false)
	private List<IPluginService> services;

	private PluginServiceHolderImpl() {
	};

	@PostConstruct
	private void init() {

		if (services == null)
			return;

		for (IPluginService service : services) {
			// přidej do registru pro parser
			pluginRegister.registerPlugin(service.getPluginFactory());

			// Editor button bundle přidej dle skupiny pluginů do tools nabídky
			addButtonToGroup(service.getEditorButtonResources());
		}
	}

	/**
	 * Pluginy dle skupin
	 */
	private Map<String, Map<String, EditorButtonResources>> editorCatalog = new HashMap<String, Map<String, EditorButtonResources>>();

	private void addButtonToGroup(EditorButtonResources resources) {

		// existuje skupina ?
		if (editorCatalog.containsKey(resources.getTagFamily())) {
			editorCatalog.get(resources.getTagFamily()).put(resources.getTag(),
					resources);
		} else {
			// založ
			Map<String, EditorButtonResources> map = new HashMap<String, EditorButtonResources>();
			map.put(resources.getTag(), resources);
			editorCatalog.put(resources.getTagFamily(), map);
		}
	}

	public synchronized Set<String> getRegisteredGroups() {
		return new HashSet<String>(editorCatalog.keySet());
	}

	public synchronized Set<EditorButtonResources> getGroupTags(String group) {
		return new HashSet<EditorButtonResources>(editorCatalog.get(group)
				.values());
	}

}
