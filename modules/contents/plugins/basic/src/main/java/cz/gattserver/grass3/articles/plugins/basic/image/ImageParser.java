package cz.gattserver.grass3.articles.plugins.basic.image;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.gattserver.grass3.articles.editor.lexer.Token;
import cz.gattserver.grass3.articles.editor.parser.Parser;
import cz.gattserver.grass3.articles.editor.parser.ParsingProcessor;
import cz.gattserver.grass3.articles.editor.parser.elements.Element;
import cz.gattserver.grass3.articles.editor.parser.exceptions.TokenException;

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

		if (!startTag.equals(tag))
			throw new TokenException(tag, startTag);

		// START_TAG byl zpracován
		pluginBag.nextToken();

		// zpracovat text
		StringBuilder link = new StringBuilder();
		if (Token.TEXT.equals(pluginBag.getToken()))
			link.append(pluginBag.getTextTree().getText());
		else
			throw new TokenException(Token.TEXT, pluginBag.getToken(), pluginBag.getText());

		// zpracovat koncový tag
		String endTag = pluginBag.getEndTag();
		logger.debug(pluginBag.getToken().toString());

		if (!endTag.equals(tag))
			throw new TokenException(tag, endTag);

		// END_TAG byl zpracován
		pluginBag.nextToken();

		return new ImageElement(link.toString());
	}
}
