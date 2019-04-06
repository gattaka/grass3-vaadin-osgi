package cz.gattserver.grass3.articles.plugins.favlink.plugin;

import cz.gattserver.grass3.articles.editor.lexer.Token;
import cz.gattserver.grass3.articles.editor.parser.Parser;
import cz.gattserver.grass3.articles.editor.parser.ParsingProcessor;
import cz.gattserver.grass3.articles.editor.parser.elements.Element;
import cz.gattserver.grass3.articles.editor.parser.exceptions.TokenException;
import cz.gattserver.grass3.articles.plugins.favlink.strategies.FaviconObtainStrategy;

/**
 * @author gatt
 */
public class FavlinkParser implements Parser {

	private String tag;
	private FaviconObtainStrategy strategy;

	public FavlinkParser(String tag, FaviconObtainStrategy strategy) {
		this.tag = tag;
		this.strategy = strategy;
	}

	@Override
	public Element parse(ParsingProcessor parsingProcessor) {

		// zpracovat počáteční tag
		String startTag = parsingProcessor.getStartTag();

		if (!startTag.equals(tag))
			throw new TokenException(tag, startTag);

		// START_TAG byl zpracován
		parsingProcessor.nextToken();

		// zpracovat text - musím zahazovat anotace pozic, střetly by se
		StringBuilder builder = new StringBuilder();
		while ((parsingProcessor.getToken() != Token.END_TAG || !parsingProcessor.getEndTag().equals(tag))
				&& parsingProcessor.getToken() != Token.EOF) {
			builder.append(parsingProcessor.getText());
			parsingProcessor.nextToken();
		}
		String text = builder.toString();

		String pageURL;
		String description = "";
		int lastSpace = text.trim().lastIndexOf(' ');
		if (lastSpace > 0) {
			description = text.substring(0, lastSpace);
			pageURL = text.substring(lastSpace + 1);
		} else {
			pageURL = text;
		}

		String faviconURL = null;
		faviconURL = strategy.obtainFaviconURL(pageURL, parsingProcessor.getContextRoot());

		// zpracovat koncový tag
		// END_TAG byl zpracován
		parsingProcessor.getEndTag();
		parsingProcessor.nextToken();

		return new FavlinkElement(faviconURL, description, pageURL);
	}
}
