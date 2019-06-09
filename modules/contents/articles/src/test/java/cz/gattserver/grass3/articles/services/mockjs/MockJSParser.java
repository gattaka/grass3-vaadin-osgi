package cz.gattserver.grass3.articles.services.mockjs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.gattserver.grass3.articles.editor.lexer.Token;
import cz.gattserver.grass3.articles.editor.parser.Parser;
import cz.gattserver.grass3.articles.editor.parser.ParsingProcessor;
import cz.gattserver.grass3.articles.editor.parser.elements.Element;
import cz.gattserver.grass3.articles.editor.parser.exceptions.ParserException;

public class MockJSParser implements Parser {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	private String tag;

	public MockJSParser(String tag) {
		this.tag = tag;
	}

	@Override
	public Element parse(ParsingProcessor pluginBag) {
		// zpracovat počáteční tag
		parseStartTag(pluginBag);

		StringBuilder code = new StringBuilder();

		// Zpracuje vnitřek tagů jako code - jedu dokud nenarazím na svůj
		// koncový tag - všechno ostatní beru jako obsah zdrojáku - text i
		// potenciální počáteční tagy. Jediná věc, která mne může zastavit je
		// EOF nebo můj koncový tag. Načítám text po řádcích protože chci
		// zachovat řádkování kódu. Jinak kód by měl být escapován.
		Token currentToken = null;
		while (true) {
			currentToken = pluginBag.getToken();
			if ((Token.END_TAG.equals(currentToken) && pluginBag.getEndTag().equals(tag))
					|| Token.EOF.equals(currentToken))
				break;
			if (Token.EOL.equals(currentToken)) {
				code.append("\n");
			} else {
				code.append(pluginBag.getCode());
			}
			pluginBag.nextToken();
		}

		// zpracovat koncový tag
		parseEndTag(pluginBag);

		return new MockJSElement(code.toString());
	}

	private void parseStartTag(ParsingProcessor pluginBag) {
		String startTag = pluginBag.getStartTag();
		if (!startTag.equals(tag)) {
			logger.warn("Čekal jsem: [" + tag + "] ne " + startTag);
			throw new ParserException();
		}
		pluginBag.nextToken();
	}

	private void parseEndTag(ParsingProcessor pluginBag) {
		String endTag = pluginBag.getEndTag();
		if (!endTag.equals(tag)) {
			logger.warn("Čekal jsem: [/" + tag + "] ne " + pluginBag.getCode());
			throw new ParserException();
		}
		pluginBag.nextToken();
	}

}
