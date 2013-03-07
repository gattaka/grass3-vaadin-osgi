package org.myftp.gattserver.grass3.articles;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.myftp.gattserver.grass3.articles.editor.api.EditorButtonResources;
import org.myftp.gattserver.grass3.articles.parser.PluginRegister;
import org.myftp.gattserver.grass3.articles.service.IPluginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("pluginServiceHolder")
public class PluginServiceHolder {

	@Resource(name = "pluginRegister")
	private PluginRegister pluginRegister;

	@Autowired
	private PluginServiceHolder(List<IPluginService> services) {

		for (IPluginService service : services) {
			// přidej do registru pro parser
			pluginRegister.registerPlugin(service.getPluginFactory());

			// Editor button bundle přidej dle skupiny pluginů do tools nabídky
			addButtonToGroup(service.getEditorButtonResources());
		}

	};

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
