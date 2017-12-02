package cz.gattserver.grass3.articles.editor.parser.impl;


import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.gattserver.grass3.articles.editor.parser.Parser;
import cz.gattserver.grass3.articles.editor.parser.ParsingProcessor;
import cz.gattserver.grass3.articles.editor.parser.elements.ArticleElement;
import cz.gattserver.grass3.articles.editor.parser.elements.Element;

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

	private ParsingProcessor parsingProcessor;

	/**
	 * Postaví strom článku a vyhodnotí chyby. Vstupní metoda pro zpracování
	 * článku.
	 */
	public Element parse(ParsingProcessor parsingProcessor) {
		this.parsingProcessor = parsingProcessor;
		parsingProcessor.nextToken();
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
		logger.debug("article: " + parsingProcessor.getToken());
		switch (parsingProcessor.getToken()) {
		case EOF:
			/**
			 * Konec článku = prázdný článek
			 */
			return new ArticleElement(null);
		default:
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
			parsingProcessor.getBlock(elist);
			return new ArticleElement(elist);
		}
	}

	@Override
	public boolean canHoldBreaklineElement() {
		return false;
	}
}
