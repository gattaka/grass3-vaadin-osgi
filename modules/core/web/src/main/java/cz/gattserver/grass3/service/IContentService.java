package cz.gattserver.grass3.service;

import com.vaadin.server.Resource;

import cz.gattserver.grass3.pages.factories.template.IPageFactory;

public interface IContentService {

	/**
	 * Vrátí instanci stránky, která slouží jako editor pro vytváření nebo
	 * modifikaci daného obsahu
	 */
	public IPageFactory getContentEditorPageFactory();

	/**
	 * Vrátí instanci stránky, která slouží jako prohlížeč obsahu
	 */
	public IPageFactory getContentViewerPageFactory();

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
