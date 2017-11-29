package cz.gattserver.grass3.articles.plugins.basic.image;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.gattserver.grass3.articles.editor.lexer.Token;
import cz.gattserver.grass3.articles.editor.parser.Parser;
import cz.gattserver.grass3.articles.editor.parser.ParsingProcessor;
import cz.gattserver.grass3.articles.editor.parser.elements.Element;
import cz.gattserver.grass3.articles.editor.parser.exceptions.ParserException;

/**
 * 
 * @author gatt
 */
public class ImageParser implements Parser {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	private String tag;

	public ImageParser(String tag) {
		this.tag = tag;
	}

	@Override
	public Element parse(ParsingProcessor pluginBag) {

		// zpracovat počáteční tag
		String startTag = pluginBag.getStartTag();
		logger.debug(pluginBag.getToken().toString());

		if (!startTag.equals(tag)) {
			logger.warn("Čekal jsem: %s, ne %s%n", '[' + tag + ']', startTag);
			throw new ParserException();
		}

		// START_TAG byl zpracován
		pluginBag.nextToken();

		// zpracovat text
		StringBuilder link = new StringBuilder();
		while ((pluginBag.getToken() != Token.END_TAG || !pluginBag.getEndTag().equals(tag))
				&& pluginBag.getToken() != Token.EOF) {
			link.append(pluginBag.getTextTree().getText());
		}

		// zpracovat koncový tag
		String endTag = pluginBag.getEndTag();
		logger.debug(pluginBag.getToken().toString());

		if (!endTag.equals(tag)) {
			logger.warn("Čekal jsem: %s, ne %s%n", "[/" + tag + ']', pluginBag.getCode());
			throw new ParserException();
		}

		// END_TAG byl zpracován
		pluginBag.nextToken();

		return new ImageElement(link.toString());
	}

	@Override
	public boolean canHoldBreakline() {
		// nemůžu vložit <br/> do <img/> elementu
		return false;
	}

}
