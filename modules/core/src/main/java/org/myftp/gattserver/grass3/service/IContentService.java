package org.myftp.gattserver.grass3.service;

import java.util.Map;

import org.myftp.gattserver.grass3.model.domain.Node;
import org.myftp.gattserver.grass3.windows.template.BaseWindow;

import com.vaadin.terminal.Resource;

public interface IContentService extends IModuleService {

	/**
	 * Vytvoří novou instanci stránky, která slouží jako editor pro vytváření
	 * nebo modifikaci daného obsahu
	 * 
	 * @param node
	 *            kategorie do/ze které je obsah ukládán/editován
	 * @param params
	 *            parametry (TODO ... možná pryč)
	 * @return instance třídy stránky s editorem pro daný obsah
	 */
	public BaseWindow createContentEditorWindow(Node node, Map<String,String[]> params);

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
	 * Vrátí třídu stránky, která slouží pro daný obsah jako editor
	 * 
	 * @return třída stránky editoru
	 */
	public Class<? extends BaseWindow> getContentEditorWindowClass();

	/**
	 * Vrátí identifikátor služby obsahu
	 * 
	 * @return identifikátor služby
	 */
	public String getContentID();

	/**
	 * Vrátí třídu stránky prohlížeče daného obsahu
	 * 
	 * @return třída stránky prohlížeče
	 */
	public Class<? extends BaseWindow> getContentViewerWindowClass();

}
