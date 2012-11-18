package org.myftp.gattserver.grass3.articles.parser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.myftp.gattserver.grass3.articles.editor.api.EditorButtonResources;
import org.myftp.gattserver.grass3.articles.parser.interfaces.IPluginFactory;



/**
 *
 * @author gatt
 */
public class PluginRegister {

    /* jméno skupiny nebo balíčku ke které elementy náleží + tagy elementů */
    private Map<String, List<EditorButtonResources>> editorCatalog = new HashMap<String, List<EditorButtonResources>>();
    private Map<String, IPluginFactory> plugins = new HashMap<String, IPluginFactory>();
    /* Singleton metody a vlastnosti */
    private static PluginRegister pluginRegister = null;

    private PluginRegister() {
    }

    public static PluginRegister getInstance() {
        if (pluginRegister == null) {
            pluginRegister = new PluginRegister();
        }
        return pluginRegister;
    }

    /**
     * Registers tag for parser and insert tags in editor tag catalog
     *     
     * @param group element's group
     * @param pluginFactory factory of the plugin to get its instances 
     * @return true if the element registration was successful, 
     * false if the tag key is occupied
     */
    public boolean registerPlugin(IPluginFactory pluginFactory, String group) {

        if (plugins.containsKey(pluginFactory.getTag())) {
            return false;
        }

        // parser register
        plugins.put(pluginFactory.getTag(), pluginFactory);
        
        EditorButtonResources ebb = pluginFactory.getEditorButtonResources();
        
        // existuje skupina ?
        if (editorCatalog.containsKey(group)) {
            editorCatalog.get(group).add(ebb);
        } else {
            // založ
            List<EditorButtonResources> list = new ArrayList<EditorButtonResources>();
            list.add(ebb);
            editorCatalog.put(group, list);
        }

        return true;

    }

    public void flushRegister() {
        plugins.clear();
        editorCatalog.clear();
    }

    public Set<String> getRegisteredTags() {
        return plugins.keySet();
    }

    public Set<String> getRegisteredGroups() {
        return editorCatalog.keySet();
    }

    public List<EditorButtonResources> getGroupTags(String group) {
        return editorCatalog.get(group);
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
}
