package org.myftp.gattserver.grass3.articles.code;

import org.myftp.gattserver.grass3.articles.lexer.Token;
import org.myftp.gattserver.grass3.articles.parser.PluginBag;
import org.myftp.gattserver.grass3.articles.parser.exceptions.ParserException;
import org.myftp.gattserver.grass3.articles.parser.interfaces.AbstractElementTree;
import org.myftp.gattserver.grass3.articles.parser.interfaces.AbstractParserPlugin;
import org.myftp.gattserver.grass3.articles.parser.misc.HTMLEscaper;

/**
 * 
 * @author gatt
 */
public class CodeElement extends AbstractParserPlugin {

	private String tag;
	private String description;

	private HighlightEngine highlightEngine;

	/**
	 * Jaký styl určuje jazky, který zvýrazním
	 */
	private String style;

	public CodeElement(String tag, String style, String description,
			HighlightEngine highlightEngine) {
		this.tag = tag;
		this.style = style;
		this.description = description;
		this.highlightEngine = highlightEngine;
	}

	public AbstractElementTree parse(PluginBag pluginBag) {

		// zpracovat počáteční tag
		String startTag = pluginBag.getStartTag();
		log(this.getClass().getSimpleName() + ": " + pluginBag.getToken());

		if (!startTag.equals(tag)) {
			log("Čekal jsem: [" + tag + "] ne " + startTag);
			throw new ParserException();
		}

		// START_TAG byl zpracován
		pluginBag.nextToken();

		StringBuilder code = new StringBuilder();

		/**
		 * Zpracuje vnitřek tagů jako code - jedu dokud nenarazím na svůj
		 * koncový tag - všechno ostatní beru jako obsah zdrojáku - text i
		 * potenciální počáteční tagy. Jediná věc, která mne může zastavit je
		 * EOF nebo můj koncový tag. Načítám text po řádcích protože chci
		 * zachovat řádkování kódu. Jinak kód by měl být escapován.
		 */
		Token lastToken = null;
		Token currentToken = null;
		int lines = 1;
		while (true) {
			currentToken = pluginBag.getToken();
			if ((currentToken == Token.END_TAG && pluginBag.getEndTag().equals(
					tag))
					|| currentToken == Token.EOF)
				break;
			/**
			 * Pokud načteš TEXT, tak přidej jeho obsah, pokud pak načtečeš EOL,
			 * tak nepřidávej prázdný řádek, ledaže by jsi načetl EOL EOL - pak
			 * je to prázdný řádek
			 */
			if (currentToken == Token.TEXT || currentToken == Token.END_TAG
					|| currentToken == Token.START_TAG)
				code.append(HTMLEscaper.stringToHTMLString(pluginBag.getCode()));
			else if (currentToken == Token.EOL) {
				// prázdné řádky je potřeba prokládat mezerou, kterou si JS záhy
				// uzavře do <p></p> elementů - bez této mezery by <p></p>
				// element neudělal odřádkování nutné a zmizeli by tak prázdné
				// řádky - po stránce korektnosti zpětného čtení z webu je vše v
				// pořádku, protože <p></p> přebytečnou mezeru záhy zase vynechá
				// ...
				if (lastToken == currentToken)
					code.append(" ");
				code.append('\n');
				lines++;
			}
			pluginBag.nextToken();
			lastToken = currentToken;
		}

		// zpracovat koncový tag
		String endTag = pluginBag.getEndTag();
		log(this.getClass().getSimpleName() + ": " + pluginBag.getToken());

		if (!endTag.equals(tag)) {
			log("Čekal jsem: [/" + tag + "], ne " + pluginBag.getCode());
			throw new ParserException();
		}

		// END_TAG byl zpracován
		pluginBag.nextToken();

		// protože za CODE je většinou mezera ignoruje se případný <br/>
		if (pluginBag.getToken().equals(Token.EOL)) {
			pluginBag.nextToken();
		}

		// position 1, position 2, link odkazu, text odkazu (optional), ikona
		// (optional), default ikona
		return new CodeTree(code.toString(), style, lines, description,
				highlightEngine);
	}

	public boolean canHoldBreakline() {
		// nemůžu vložit <br/> do tabulky kodu
		return false;
	}
}