package cz.gattserver.grass3.modules;

import com.vaadin.server.Resource;

import cz.gattserver.grass3.ui.pages.factories.template.PageFactory;

public interface ContentModule {

	/**
	 * Vrátí instanci stránky, která slouží jako editor pro vytváření nebo
	 * modifikaci daného obsahu
	 */
	PageFactory getContentEditorPageFactory();

	/**
	 * Vrátí instanci stránky, která slouží jako prohlížeč obsahu
	 */
	PageFactory getContentViewerPageFactory();

	/**
	 * Vrátí popisek k tlačítku "vytvořit nový obsah"
	 * 
	 * @return popisek ve stylu "článek", aby to pasovalo k popisku "Vytvořit
	 *         nový"
	 */
	String getCreateNewContentLabel();

	/**
	 * Vrátí cestu k ikoně, kterou bude obsah reprezentován
	 * 
	 * @return cesta k ikoně obsahu
	 */
	Resource getContentIcon();

	/**
	 * Vrátí identifikátor služby obsahu
	 * 
	 * @return identifikátor služby
	 */
	String getContentID();

}
