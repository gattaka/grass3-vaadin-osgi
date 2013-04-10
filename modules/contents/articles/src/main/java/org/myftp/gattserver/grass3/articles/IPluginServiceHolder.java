package org.myftp.gattserver.grass3.articles;

import java.util.Set;

import org.myftp.gattserver.grass3.articles.editor.api.EditorButtonResources;

public interface IPluginServiceHolder {

	public Set<String> getRegisteredGroups();

	public Set<EditorButtonResources> getGroupTags(String group);

}
