package cz.gattserver.grass3.articles.services.mock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.gattserver.grass3.articles.editor.lexer.Token;
import cz.gattserver.grass3.articles.editor.parser.Parser;
import cz.gattserver.grass3.articles.editor.parser.PluginBag;
import cz.gattserver.grass3.articles.editor.parser.elements.Element;
import cz.gattserver.grass3.articles.editor.parser.exceptions.ParserException;

public class MockParser implements Parser {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	private String tag;
	private int numberOfStars;

	public MockParser(String tag) {
		this.tag = tag;
	}

	@Override
	public Element parse(PluginBag pluginBag) {
		// zpracovat počáteční tag
		parseStartTag(pluginBag);

		// počet
		parseCount(pluginBag);

		// zpracovat koncový tag
		parseEndTag(pluginBag);

		return new MockElement(numberOfStars);
	}

	@Override
	public boolean canHoldBreakline() {
		// ne
		return false;
	}

	private void parseStartTag(PluginBag pluginBag) {
		String startTag = pluginBag.getStartTag();
		if (!startTag.equals(tag)) {
			logger.warn("Čekal jsem: [" + tag + "] ne " + startTag);
			throw new ParserException();
		}
		pluginBag.nextToken();
	}

	private void parseCount(PluginBag pluginBag) {
		if (pluginBag.getToken() != Token.EOF) {
			try {
				numberOfStars = Integer.parseInt(pluginBag.getText());
			} catch (NumberFormatException e) {
				throw new ParserException(e);
			}
		} else {
			throw new ParserException();
		}
		pluginBag.nextToken();
	}

	private void parseEndTag(PluginBag pluginBag) {
		String endTag = pluginBag.getEndTag();
		if (!endTag.equals(tag)) {
			logger.warn("Čekal jsem: [/" + tag + "] ne " + pluginBag.getCode());
			throw new ParserException();
		}
		pluginBag.nextToken();
	}

}
