package cz.gattserver.grass3.articles.services;

/**
 * Jaký způsobem se má zpracovat článek
 * 
 * @author Hynek
 *
 */
public enum ArticleProcessMode {

	/**
	 * Přelož článek a ulož ho
	 */
	FULL, 
	
	/**
	 * Ulož článek jako rozpracovaný, není potřeba ho překládat
	 */
	DRAFT, 
	
	/**
	 * Ulož článek jako rozpracovaný a přelož ho pro náhled
	 */
	PREVIEW
}
