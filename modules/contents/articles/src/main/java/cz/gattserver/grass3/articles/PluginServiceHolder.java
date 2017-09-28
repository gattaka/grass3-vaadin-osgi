package cz.gattserver.grass3.articles;

import java.util.Set;

import cz.gattserver.grass3.articles.editor.api.EditorButtonResources;

public interface PluginServiceHolder {

	public Set<String> getRegisteredGroups();

	public Set<EditorButtonResources> getGroupTags(String group);

}
