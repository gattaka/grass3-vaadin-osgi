package cz.gattserver.grass3.articles.service;

import cz.gattserver.grass3.articles.editor.api.EditorButtonResources;
import cz.gattserver.grass3.articles.parser.interfaces.PluginFactory;

/**
 * Rozhraní prvku editoru článků - pluginu
 * 
 * @author gatt
 * 
 */
public interface PluginService {

	/**
	 * Získá factory pro vytváření parsovací části pluginu
	 * 
	 * @return
	 */
	public PluginFactory getPluginFactory();

	/**
	 * Získá zdroje pro vytvoření odpovídajícího tlačítka pluginu v editoru
	 * 
	 * @return
	 */
	public EditorButtonResources getEditorButtonResources();

}
