package org.myftp.gattserver.grass3.articles.parser;

import java.util.ArrayList;
import java.util.List;

import org.myftp.gattserver.grass3.articles.parser.elements.ArticleTree;
import org.myftp.gattserver.grass3.articles.parser.elements.ParserError;
import org.myftp.gattserver.grass3.articles.parser.interfaces.AbstractElementTree;
import org.myftp.gattserver.grass3.articles.parser.interfaces.AbstractParser;


import static org.myftp.gattserver.grass3.articles.lexer.Token.*;

/**
 * 
 * STRATEGIE NOVÝCH ŘÁDKŮ: - pakliže řádek končí normálně \n a přitom obsahuje
 * nějaký text, vytvoří se pouze odrážka <br/>
 * - tato akce se dá zakázat pomocí flagu (takový table určitě nechce aby tam
 * byly <br/>
 * symboly ve zdrojáku - tato kontrola se zjišťuje z bloku newline, protože se
 * tak eliminuje chyba na straně pluginu, kdyby zapomněl flag znovu otevřít -
 * pakliže je po nějakém EOL_* tokenu symbol EL (empty line), je to vyhodnoceno
 * jako oddělení odstavců - drží se flag o tom, zda se píše odstavec a pokud
 * ano, je tato kombinace tokenů vyhodnocena jako </p> - jinak se tam položí za
 * EOL_* jedno <br/>
 * a za každý EL další <br/>
 * - pakliže se nepíše odstavec, je při prvním možném elementu (zda jsou
 * odstavce povolené se zjišťuje stejně jako zda jsou povolené <br/>
 * symboly) vysázen prvek
 * <p>
 * 
 * @author gatt
 */
public class ArticleParser extends AbstractParser {

	private PluginBag pluginBag;

	/**
	 * Postaví strom článku a vyhodnotí chyby. Vstupní metoda pro zpracování
	 * článku.
	 */
	public AbstractElementTree parse(PluginBag pluginBag) {
		this.pluginBag = pluginBag;

		pluginBag.nextToken();

		ArticleTree p = article();
		return p;
	}

	/**
	 * Kořenový element - article. Vyhodnocuje nic jako prázdný obsah, začátek
	 * tagu nebo text jako blok nebo koncový tag jako chybu
	 * 
	 * @return strom článku
	 */
	private ArticleTree article() {
		log(this.getClass().getSimpleName() + ": article: " + pluginBag.getToken());
		switch (pluginBag.getToken()) {
		case EOF:
			/**
			 * Konec článku = prázdný článek
			 */
			return new ArticleTree(null);

		case EOL:
		case START_TAG:
		case TEXT:
		case END_TAG:
			/**
			 * Konec řádky (ne článku), počáteční tag, text - zpracuj obsah jako
			 * blok elementů a textu - vyrob si list do kterého se budou
			 * nalezené a zpracované AST elementů přidávat.
			 * 
			 * Koncový tag bude možná chyba, protože jsem ještě nenašel žádný
			 * počáteční, ale protože Lexer nebere v úvahu sémantiku vstupu, tak
			 * to může být klidně text protože [/ssss] není třeba tag žádného
			 * pluginu, tudíž to není chyba, že tím článek začíná
			 */
			List<AbstractElementTree> elist = new ArrayList<AbstractElementTree>();
			pluginBag.getBlock(elist);
			return new ArticleTree(elist);

		default:
			log("Čekal jsem: " + EOL + "," + EOF + "," + START_TAG + " nebo " +
					TEXT + " ale dostal jsem " + pluginBag.getToken());
			List<AbstractElementTree> errorList = new ArrayList<AbstractElementTree>();
			errorList.add(new ParserError("Article"));
			return new ArticleTree(errorList);
		}
	}
}
