package cz.gattserver.grass3.articles.plugins.headers;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.gattserver.grass3.articles.editor.lexer.Token;
import cz.gattserver.grass3.articles.editor.parser.Parser;
import cz.gattserver.grass3.articles.editor.parser.ParsingProcessor;
import cz.gattserver.grass3.articles.editor.parser.elements.Element;
import cz.gattserver.grass3.articles.editor.parser.exceptions.TokenException;
import cz.gattserver.grass3.articles.plugins.headers.HeaderElement;

/**
 * @author gatt
 */
public class HeaderParser implements Parser {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	private int level;
	private String tag;

	public HeaderParser(int level, String tag) {
		this.level = level;
		this.tag = tag;
	}

	@Override
	public Element parse(ParsingProcessor pluginBag) {

		// zpracovat počáteční tag
		String startTag = pluginBag.getStartTag();
		logger.debug(pluginBag.getToken().toString());

		if (!startTag.equals(tag))
			throw new TokenException(tag, startTag);

		// START_tag byl zpracován
		pluginBag.nextToken();

		// zpracovat text
		List<Element> elist = new ArrayList<Element>();
		pluginBag.getBlock(elist, tag);
		// nextToken() - je již voláno v text() !!!

		// zpracovat koncový tag
		String endTag = pluginBag.getEndTag();
		if (!endTag.equals(tag))
			throw new TokenException(tag, endTag);

		// END_tag byl zpracován
		pluginBag.nextToken();

		// protože za H je mezera ignoruje se případný <br/>
		if (pluginBag.getToken().equals(Token.EOL))
			pluginBag.nextToken();

		return new HeaderElement(elist, level);
	}
}
