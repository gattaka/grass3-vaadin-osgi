package org.myftp.gattserver.grass3.articles.service;

import com.vaadin.terminal.Resource;

/**
 * Rozhraní prvku editoru článků - pluginu
 * 
 * @author gatt
 * 
 */
public interface IPluginService {

	/**
	 * Získá jméno pluginu
	 */
	public String getPluginName();

	/**
	 * Získá popisek vkládacího tlačítka pluginu do editoru
	 */
	public String getPluginButtonCaption();

	/**
	 * Získá resource pro ikonu tlačítka pluginu do editoru
	 */
	public Resource getPluginButtonImageResource();

	/**
	 * Získá dekorátor vybraného textu
	 */
	public ISelectionDecorator getPluginSelectionDecorator();
	
	/**
	 * TODO
	 */

}
