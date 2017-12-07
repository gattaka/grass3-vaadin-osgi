package cz.gattserver.grass3.articles.plugins.favlink.plugin;

import java.util.List;

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
	private List<FaviconObtainStrategy> strategies;

	public FavlinkParser(String tag, List<FaviconObtainStrategy> strategies) {
		this.tag = tag;
		this.strategies = strategies;
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
		String pageURL = builder.toString();

		String faviconURL = null;
		for (FaviconObtainStrategy s : strategies) {
			faviconURL = s.obtainFaviconURL(pageURL.toString(), parsingProcessor.getContextRoot());
			if (faviconURL != null)
				break;
		}

		// zpracovat koncový tag
		// END_TAG byl zpracován
		parsingProcessor.getEndTag();
		parsingProcessor.nextToken();

		return new FavlinkElement(faviconURL, pageURL);
	}
}
