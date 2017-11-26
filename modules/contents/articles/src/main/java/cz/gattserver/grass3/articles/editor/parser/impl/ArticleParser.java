package cz.gattserver.grass3.articles.editor.parser.impl;

import static cz.gattserver.grass3.articles.editor.lexer.Token.*;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.gattserver.grass3.articles.editor.parser.Parser;
import cz.gattserver.grass3.articles.editor.parser.PluginBag;
import cz.gattserver.grass3.articles.editor.parser.elements.ArticleElement;
import cz.gattserver.grass3.articles.editor.parser.elements.Element;
import cz.gattserver.grass3.articles.editor.parser.elements.ParserErrorElement;

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
 * ano, je tato kombinace tokenů vyhodnocena jako
 * </p>
 * - jinak se tam položí za EOL_* jedno <br/>
 * a za každý EL další <br/>
 * - pakliže se nepíše odstavec, je při prvním možném elementu (zda jsou
 * odstavce povolené se zjišťuje stejně jako zda jsou povolené <br/>
 * symboly) vysázen prvek
 * <p>
 * 
 * @author gatt
 */
public class ArticleParser implements Parser {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	private PluginBag pluginBag;

	/**
	 * Postaví strom článku a vyhodnotí chyby. Vstupní metoda pro zpracování
	 * článku.
	 */
	public Element parse(PluginBag pluginBag) {
		this.pluginBag = pluginBag;
		pluginBag.nextToken();
		ArticleElement p = article();
		return p;
	}

	/**
	 * Kořenový element - article. Vyhodnocuje nic jako prázdný obsah, začátek
	 * tagu nebo text jako blok nebo koncový tag jako chybu
	 * 
	 * @return strom článku
	 */
	private ArticleElement article() {
		logger.debug("article: " + pluginBag.getToken());
		switch (pluginBag.getToken()) {
		case EOF:
			/**
			 * Konec článku = prázdný článek
			 */
			return new ArticleElement(null);
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
			List<Element> elist = new ArrayList<Element>();
			pluginBag.getBlock(elist);
			return new ArticleElement(elist);
		default:
			logger.warn("Čekal jsem: " + EOL + "," + EOF + "," + START_TAG + " nebo " + TEXT + " ale dostal jsem "
					+ pluginBag.getToken());
			List<Element> errorList = new ArrayList<Element>();
			errorList.add(new ParserErrorElement("Article"));
			return new ArticleElement(errorList);
		}
	}

	@Override
	public boolean canHoldBreakline() {
		return false;
	}
}
