package org.myftp.gattserver.grass3.service;

/**
 * Interface, který musí splňovat každá modulární součást Grass systému.
 * 
 * @author gatt
 * 
 */
public interface IModuleService {

	/**
	 * Jaká nejvyšší verze Grass je potřeba aby modul fungoval ?
	 * 
	 * @return číslo verze nebo <b>{@code null}</b> pokud není omezeno
	 */
	public String getMaxRequiredVersion();

	/**
	 * Jaká nejnižší verze Grass je potřeba aby modul fungoval ?
	 * 
	 * @return číslo verze nebo <b>{@code null}</b> pokud není omezeno
	 */
	public String getMinRequiredVersion();

	/**
	 * Jaká je verze modulu ?
	 * 
	 * @return číslo verze modulu
	 */
	public String getVersion();

	/**
	 * Vrátí jméno modulu - toto jméno slouží pouze pro zobrazování v logu a
	 * přehledu, nevyhodnocuje se dle něj zpracovatel obsahu apod. - je nicméně
	 * vhodné aby dostatečně unikátně identifikovalo modul.
	 * 
	 * @return název modulu
	 */
	public String getName();

}
