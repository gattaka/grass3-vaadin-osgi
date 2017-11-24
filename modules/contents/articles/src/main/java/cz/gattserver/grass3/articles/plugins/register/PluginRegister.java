package cz.gattserver.grass3.articles.plugins.register;

import java.util.Set;

import cz.gattserver.grass3.articles.editor.parser.interfaces.EditorButtonResourcesTO;
import cz.gattserver.grass3.articles.plugins.Plugin;

/**
 * @author gatt
 */
public interface PluginRegister {

	boolean registerPlugin(Plugin plugin);

	Set<String> getRegisteredTags();

	boolean isRegistered(String tag);

	Plugin get(String tag);

	Plugin unregisterPlugin(String tag);

	Set<String> getRegisteredGroups();

	Set<EditorButtonResourcesTO> getGroupTags(String group);
}
