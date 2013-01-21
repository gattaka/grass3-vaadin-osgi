package org.myftp.gattserver.grass3.articles.parser.interfaces;

import java.util.Set;

/**
 * Kontext pouzity pri generovani kodu.
 */
public interface IContext {

	/**
	 * Zapíše do výstupu
	 * 
	 * @param msg
	 */
	public void print(String msg);

	/**
	 * Zapíše do výstupu a ukončí novou řádkou
	 * 
	 * @param msg
	 */
	public void println(String msg);

	/**
	 * Nastaví úroveň textu dle nadpisu (1-4)
	 * 
	 * @param level
	 */
	public void setHeaderLevel(int level);

	/**
	 * Vyresetuje úroveň nadpisu
	 */
	public void resetHeaderLevel();

	/**
	 * Vytvoří finální výstup
	 * 
	 * @return výstup článku
	 */
	public String getOutput();

	/**
	 * Zaregistruje CSS zdroj, který je potřeba aby systém přidal při
	 * zobrazování článku s tímto pluginem
	 * 
	 * @param url místo na které se má odkázat 
	 */
	public void addCSSResource(String url);
	
	/**
	 * Zaregistruje JS zdroj, který je potřeba aby systém přidal při
	 * zobrazování článku s tímto pluginem
	 * 
	 * @param url místo na které se má odkázat 
	 */
	public void addJSResource(String url);
		
	/**
	 * Získá CSS zdroje, potřebné pro korektní zobrazení tohoto článku
	 * 
	 * @return místa zdrojů
	 */
	public Set<String> getCSSResources();
	
	/**
	 * Získá JS zdroje, potřebné pro korektní zobrazení tohoto článku
	 * 
	 * @return místa zdrojů
	 */
	public Set<String> getJSResources();

}
