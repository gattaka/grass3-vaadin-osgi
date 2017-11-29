package cz.gattserver.grass3.articles.editor.parser;

import cz.gattserver.grass3.articles.editor.parser.elements.Element;

/**
 * @author gatt
 */
public interface Parser {

	/**
	 * Projde článek a vytvoří {@link Element}
	 * 
	 * @param pluginBag
	 *            objekt s daty, která je předáván mezi parser pluginy
	 * @return {@link Element} AST strom pro finální generování výsledného
	 *         článku
	 */
	Element parse(ParsingProcessor pluginBag);

	/**
	 * Zjistí od parseru jestli bere konce řádků
	 * 
	 * @return {@code true} pokud je povolen v pluginu <br/>
	 */
	boolean canHoldBreakline();

}
