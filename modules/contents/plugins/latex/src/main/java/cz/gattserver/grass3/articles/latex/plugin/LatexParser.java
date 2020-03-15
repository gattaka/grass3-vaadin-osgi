package cz.gattserver.grass3.articles.latex.plugin;

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
public class LatexParser implements Parser {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	private String tag;

	public LatexParser(String tag) {
		this.tag = tag;
	}

	protected String decorateFormula(String formula) {
		return formula;
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

		StringBuilder formulaBuilder = new StringBuilder();

		/**
		 * Zpracuje vnitřek tagů jako code - jedu dokud nenarazím na svůj
		 * koncový tag - všechno ostatní beru jako obsah latex zdrojáku - text i
		 * potenciální počáteční tagy. Jediná věc, která mne může zastavit je
		 * EOF nebo můj koncový tag.
		 */
		Token currentToken = null;
		while (true) {
			currentToken = pluginBag.getToken();
			if ((currentToken == Token.END_TAG && pluginBag.getEndTag().equals(tag)) || currentToken == Token.EOF)
				break;
			if (Token.EOL.equals(currentToken)) {
				formulaBuilder.append('\n');
			} else {
				formulaBuilder.append(pluginBag.getCode());
			}
			pluginBag.nextToken();
		}

		String formula = decorateFormula(formulaBuilder.toString());

		// zpracovat koncový tag
		String endTag = pluginBag.getEndTag();
		logger.debug(pluginBag.getToken().toString());

		if (!endTag.equals(tag)) {
			logger.warn("Čekal jsem: [/" + tag + "], ne " + pluginBag.getCode());
			throw new ParserException();
		}

		// END_TAG byl zpracován
		pluginBag.nextToken();

		return new LatexElement(formula);
	}
}
