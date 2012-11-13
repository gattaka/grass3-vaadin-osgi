package org.myftp.gattserver.grass3.service;

import org.myftp.gattserver.grass3.model.domain.Node;
import org.myftp.gattserver.grass3.windows.template.BaseWindow;

import com.vaadin.terminal.Resource;

public interface IContentService {

	/**
	 * Vrátí instanci okna, které slouží jako editor pro vytváření nebo
	 * modifikaci daného obsahu
	 * 
	 * @param node
	 *            kategorie do/ze které je obsah ukládán/editován
	 */
	public BaseWindow getContentEditorWindow(Node node);

	/**
	 * Vrátí třídu okna, které slouží jako editor pro vytváření nebo modifikaci
	 * daného obsahu
	 */
	public Class<? extends BaseWindow> getContentEditorWindow();

	/**
	 * Vrátí okna prohlížeče daného obsahu
	 */
	public Class<? extends BaseWindow> getContentViewerWindow();

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
