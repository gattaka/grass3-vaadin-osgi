package cz.gattserver.grass3.articles.code;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.gattserver.grass3.articles.editor.lexer.Token;
import cz.gattserver.grass3.articles.editor.parser.Parser;
import cz.gattserver.grass3.articles.editor.parser.PluginBag;
import cz.gattserver.grass3.articles.editor.parser.elements.Element;
import cz.gattserver.grass3.articles.editor.parser.exceptions.ParserException;
import cz.gattserver.grass3.articles.editor.parser.util.HTMLEscaper;

/**
 * 
 * @author gatt
 */
public class CodeParser implements Parser {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	private String tag;
	private String description;
	private String lib;
	private String mode;

	public CodeParser(String tag, String description, String lib, String mode) {
		this.tag = tag;
		this.description = description;
		this.lib = lib;
		this.mode = mode;
	}

	@Override
	public Element parse(PluginBag pluginBag) {

		// zpracovat počáteční tag
		String startTag = pluginBag.getStartTag();
		logger.debug(pluginBag.getToken().toString());

		if (!startTag.equals(tag)) {
			logger.warn("Čekal jsem: [" + tag + "] ne " + startTag);
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
		while (true) {
			currentToken = pluginBag.getToken();
			if ((currentToken == Token.END_TAG && pluginBag.getEndTag().equals(tag)) || currentToken == Token.EOF)
				break;
			/**
			 * Pokud načteš TEXT, tak přidej jeho obsah, pokud pak načtečeš EOL,
			 * tak nepřidávej prázdný řádek, ledaže by jsi načetl EOL EOL - pak
			 * je to prázdný řádek
			 */
			if (currentToken == Token.TEXT || currentToken == Token.END_TAG || currentToken == Token.START_TAG)
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
			}
			pluginBag.nextToken();
			lastToken = currentToken;
		}

		// zpracovat koncový tag
		String endTag = pluginBag.getEndTag();
		logger.debug(pluginBag.getToken().toString());

		if (!endTag.equals(tag)) {
			logger.warn("Čekal jsem: [/" + tag + "], ne " + pluginBag.getCode());
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
		return new CodeElement(code.toString(), description, lib, mode);
	}

	@Override
	public boolean canHoldBreakline() {
		// nemůžu vložit <br/> do tabulky kodu
		return false;
	}
}