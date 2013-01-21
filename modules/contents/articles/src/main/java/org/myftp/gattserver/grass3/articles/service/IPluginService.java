package org.myftp.gattserver.grass3.articles.service;

import org.myftp.gattserver.grass3.articles.editor.api.EditorButtonResources;
import org.myftp.gattserver.grass3.articles.parser.interfaces.IPluginFactory;

/**
 * Rozhraní prvku editoru článků - pluginu
 * 
 * @author gatt
 * 
 */
public interface IPluginService {

	/**
	 * Získá factory pro vytváření parsovací části pluginu
	 * 
	 * @return
	 */
	public IPluginFactory getPluginFactory();

	/**
	 * Získá zdroje pro vytvoření odpovídajícího tlačítka pluginu v editoru
	 * 
	 * @return
	 */
	public EditorButtonResources getEditorButtonResources();

}
