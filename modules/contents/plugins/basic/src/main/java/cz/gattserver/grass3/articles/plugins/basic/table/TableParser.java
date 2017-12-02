package cz.gattserver.grass3.articles.plugins.basic.table;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.gattserver.grass3.articles.editor.lexer.Token;
import cz.gattserver.grass3.articles.editor.parser.Parser;
import cz.gattserver.grass3.articles.editor.parser.ParsingProcessor;
import cz.gattserver.grass3.articles.editor.parser.elements.Element;
import cz.gattserver.grass3.articles.editor.parser.exceptions.ParserException;

/**
 * @author gatt
 */
public class TableParser implements Parser{

	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	private String tag;
	private boolean withHead;

	/**
	 * Kolik bylo zatím max sloupec ? A kolik je aktuálně sloupců ?
	 */
	private int maxCols = 1;
	private int colsSoFar = 0;

	public TableParser(String tag, boolean withHead) {
		this.withHead = withHead;
		this.tag = tag;
	}

	@Override
	public Element parse(ParsingProcessor pluginBag) {

		// zpracovat počáteční tag
		String startTag = pluginBag.getStartTag();

		if (!startTag.equals(tag)) {
			logger.warn("Čekal jsem: [" + tag + "] ne " + startTag);
			throw new ParserException();
		}

		// START_TAG byl zpracován
		pluginBag.nextToken();

		/**
		 * Zpracovat položek listu, nemůžu volat blok, protože ten končí až na
		 * mém koncovém tagu, kdežto já potřebuju odlišit položky tabulky konci
		 * řádků
		 */
		List<List<Element>> tableElements = new ArrayList<List<Element>>();
		List<Element> elist = new ArrayList<Element>();
		while ((pluginBag.getToken() != Token.EOF)
				&& (pluginBag.getToken() != Token.END_TAG || !pluginBag
						.getEndTag().equals(tag))) {
			switch (pluginBag.getToken()) {
			/**
			 * V elementu listu můžou být jiné pluginy
			 */
			case START_TAG:
				elist.add(pluginBag.getElement());
				break;
			/**
			 * Jinak to načítám jako text
			 */
			case END_TAG:
			case TEXT:
				elist.add(pluginBag.getTextTree());
				break;
			/**
			 * Konec řádku značí konec položky tabulky
			 */
			case EOL:
				tableElements.add(elist);
				/**
				 * Pokud předchozí řádek byl prázdný, značí to konec řádky v
				 * tabulce, takže bych měl spočítat sloupce
				 */
				if (elist.isEmpty()) {
					maxCols = colsSoFar > maxCols ? colsSoFar : maxCols;
					colsSoFar = 0;
				} else {
					colsSoFar++;
				}
				elist = new ArrayList<Element>();
				pluginBag.nextToken();
				break;
			default:
				break;
			}
		}

		// po ukončení může být ještě něco nepřidáno - přidej to
		if (!elist.isEmpty()) {
			colsSoFar++;
			maxCols = colsSoFar > maxCols ? colsSoFar : maxCols;
			tableElements.add(elist);
		}

		// zpracovat koncový tag
		String endTag = pluginBag.getEndTag();

		if (!endTag.equals(tag)) {
			logger.warn("Čekal jsem: [/" + tag + "] ne " + pluginBag.getCode());
			throw new ParserException();
		}

		// END_TAG byl zpracován
		pluginBag.nextToken();

		// protože za tabulkou je mezera ignoruje se případný <br/>
		if (pluginBag.getToken().equals(Token.EOL)) {
			pluginBag.nextToken();
		}

		return new TableElement(tableElements, withHead, maxCols);
	}

	@Override
	public boolean canHoldBreaklineElement() {
		// nemůžu vložit <br/> do <a></a> elementu
		return false;
	}
}
