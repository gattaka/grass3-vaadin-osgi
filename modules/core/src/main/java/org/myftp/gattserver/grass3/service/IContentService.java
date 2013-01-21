package org.myftp.gattserver.grass3.service;

import org.myftp.gattserver.grass3.windows.template.BaseWindow;

import com.vaadin.terminal.Resource;

public interface IContentService {

	/**
	 * Vrátí instanci okna, které slouží jako editor pro vytváření nebo
	 * modifikaci daného obsahu
	 */
	public BaseWindow getContentEditorWindowNewInstance();

	/**
	 * Vrátí instanci okna, které slouží jako prohlížeč obsahu
	 */
	public BaseWindow getContentViewerWindowNewInstance();

	/**
	 * Vrátí třídu okna, které slouží jako editor pro vytváření nebo modifikaci
	 * daného obsahu
	 */
	public Class<? extends BaseWindow> getContentEditorWindowClass();

	/**
	 * Vrátí okna prohlížeče daného obsahu
	 */
	public Class<? extends BaseWindow> getContentViewerWindowClass();

	/**
	 * Vrátí popisek k tlačítku "vytvořit nový obsah"
	 * 
	 * @return popisek ve stylu "článek", aby to pasovalo k popisku
	 *         "Vytvořit nový"
	 */
	public String getCreateNewContentLabel();

	/**
	 * Vrátí cestu k ikoně, kterou bude obsah reprezentován
	 * 
	 * @return cesta k ikoně obsahu
	 */
	public Resource getContentIcon();

	/**
	 * Vrátí identifikátor služby obsahu
	 * 
	 * @return identifikátor služby
	 */
	public String getContentID();

}
