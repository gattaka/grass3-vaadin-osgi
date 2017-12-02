package cz.gattserver.grass3.articles.favlink.plugin;

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
public class LinkParser implements Parser {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	private String tag;

	public LinkParser(String tag) {
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

		StringBuilder link = new StringBuilder();

		// zpracovat text - musím zahazovat anotace pozic, střetly by se
		while ((pluginBag.getToken() != Token.END_TAG || !pluginBag.getEndTag().equals(tag))
				&& pluginBag.getToken() != Token.EOF) {
			link.append(pluginBag.getText());
			pluginBag.nextToken();
		}

		// zpracovat koncový tag
		String endTag = pluginBag.getEndTag();

		if (!endTag.equals(tag)) {
			logger.warn("Čekal jsem: [/" + tag + "] ne " + pluginBag.getCode());
			throw new ParserException();
		}

		// END_TAG byl zpracován
		pluginBag.nextToken();

		return new LinkElement(link.toString(), pluginBag.getContextRoot());
	}

	@Override
	public boolean canHoldBreaklineElement() {
		// nemůžu vložit <br/> do <a></a> elementu
		return false;
	}
}
