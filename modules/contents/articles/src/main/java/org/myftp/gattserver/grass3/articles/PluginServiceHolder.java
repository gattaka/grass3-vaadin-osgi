package org.myftp.gattserver.grass3.articles;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.myftp.gattserver.grass3.articles.editor.api.EditorButtonResources;
import org.myftp.gattserver.grass3.articles.parser.PluginRegister;
import org.myftp.gattserver.grass3.articles.service.IPluginService;

public class PluginServiceHolder {

	/**
	 * Musí být singleton, aby bylo možné jednoduše volat jeho instanci
	 * odkudkoliv z programu, zatímco vytvoření instance bude mít na starost
	 * Blueprint
	 */
	private static PluginServiceHolder instance;

	private PluginServiceHolder() {
	};

	/**
	 * TODO Tady mám trochu pochybnosti, jestli by to nešlo napsat lépe.
	 * Aktuální implementace spoléhá na to, že tuto metodu zavolá jako první
	 * Blueprint kontejner. V případě, že by to dělalo problémy mě napadá udělat
	 * místo "factory" metody factory třídu (která bude jinak ven neviditelná) a
	 * nějak to přes to vyřešit.
	 */
	public synchronized static PluginServiceHolder getInstance() {
		if (instance == null)
			instance = new PluginServiceHolder();
		return instance;
	}

	/**
	 * Pluginy dle skupin
	 */
	private Map<String, Map<String, EditorButtonResources>> editorCatalog = new HashMap<String, Map<String, EditorButtonResources>>();

	private void addButtonToGroup(EditorButtonResources resources) {

		// existuje skupina ?
		if (editorCatalog.containsKey(resources.getTagFamily())) {
			editorCatalog.get(resources.getTagFamily()).put(
					resources.getTag(), resources);
		} else {
			// založ
			Map<String, EditorButtonResources> map = new HashMap<String, EditorButtonResources>();
			map.put(resources.getTag(), resources);
			editorCatalog.put(resources.getTagFamily(), map);
		}
	}

	private void removeButtonFromGroup(EditorButtonResources resources) {
		if (editorCatalog.containsKey(resources.getTagFamily())) {
			Map<String, EditorButtonResources> group = editorCatalog
					.get(resources.getTagFamily());
			if (group.remove(resources.getTag()) != null
					&& group.size() == 0)
				editorCatalog.remove(resources.getTagFamily());
		}
	}

	public synchronized Set<String> getRegisteredGroups() {
		return new HashSet<String>(editorCatalog.keySet());
	}

	public synchronized Set<EditorButtonResources> getGroupTags(String group) {
		return new HashSet<EditorButtonResources>(editorCatalog.get(group)
				.values());
	}

	/**
	 * Pluginy
	 */
	private List<IPluginService> pluginServices = Collections
			.synchronizedList(new ArrayList<IPluginService>());

	public synchronized List<IPluginService> getPluginServices() {
		return pluginServices;
	}

	public synchronized void setPluginServices(
			List<IPluginService> pluginServices) {
		this.pluginServices = pluginServices;
	}

	public synchronized void bindPluginService(IPluginService pluginService) {

		// přidej do registru pro parser
		PluginRegister.INSTANCE
				.registerPlugin(pluginService.getPluginFactory());

		// Editor button bundle přidej dle skupiny pluginů do tools nabídky
		addButtonToGroup(pluginService.getEditorButtonResources());

	}

	public synchronized void unbindPluginService(IPluginService pluginService) {

		// parser registr
		PluginRegister.INSTANCE.unregisterPlugin(pluginService
				.getPluginFactory().getTag());

		// Editor button bundle
		removeButtonFromGroup(pluginService.getEditorButtonResources());
	}
}
