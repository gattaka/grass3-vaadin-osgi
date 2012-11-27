package org.myftp.gattserver.grass3.articles.service;

/**
 * Dekorátor pluginu editoru článků. Třída implementující toto rozhraní musí
 * umět vzít vybraný text z editoru (který je jí předán) a obalit ho tagy a
 * dalšími prvky, dle povahy pluginu.
 * 
 * @author gatt
 * 
 */
public interface ISelectionDecorator {

	/**
	 * Vezme vstupní text z editoru - výběr - a zpracuje ho (obalí) tagy a
	 * dalšími elementy dle povahy pluginu
	 * 
	 * @param selection
	 *            vybraný text z okna editoru
	 * @return obalený text z okna editoru dle povahy pluginu
	 */
	public String decorate(String selection);

}
