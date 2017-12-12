package cz.gattserver.grass3.articles.templates.sources;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.gattserver.grass3.articles.editor.lexer.Token;
import cz.gattserver.grass3.articles.editor.parser.Parser;
import cz.gattserver.grass3.articles.editor.parser.ParsingProcessor;
import cz.gattserver.grass3.articles.editor.parser.elements.Element;
import cz.gattserver.grass3.articles.editor.parser.exceptions.ParserException;
import cz.gattserver.grass3.articles.plugins.favlink.strategies.FaviconObtainStrategy;

/**
 * @author gatt
 */
public class SourcesParser implements Parser {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	private String tag;
	private List<String> faviconURLs = new ArrayList<>();
	private List<String> pageURLs = new ArrayList<>();
	private FaviconObtainStrategy strategy;

	public SourcesParser(String tag, FaviconObtainStrategy strategy) {
		this.tag = tag;
		this.strategy = strategy;
	}

	@Override
	public Element parse(ParsingProcessor pluginBag) {
		// zpracovat počáteční tag
		parseStartTag(pluginBag);

		// zpracuje zdroje
		parseSources(pluginBag);

		// zpracovat koncový tag
		parseEndTag(pluginBag);
		return new SourcesElement(faviconURLs, pageURLs);
	}

	private void parseStartTag(ParsingProcessor pluginBag) {
		String startTag = pluginBag.getStartTag();

		if (!startTag.equals(tag)) {
			logger.warn("Čekal jsem: [" + tag + "] ne " + startTag);
			throw new ParserException();
		}

		pluginBag.nextToken();
	}

	private void parseSources(ParsingProcessor parsingProcessor) {
		// dokud není konec souboru nebo můj uzavírací tag
		StringBuilder builder = new StringBuilder();
		while (parsingProcessor.getToken() != Token.EOF && (parsingProcessor.getToken() != Token.END_TAG
				|| parsingProcessor.getEndTag().equals(tag) == false)) {
			if (parsingProcessor.getToken() == Token.EOL && builder.length() != 0) {
				processURL(builder.toString(), parsingProcessor);
				builder = new StringBuilder();
			} else {
				String text = parsingProcessor.getText();
				if (text != null && text.isEmpty() == false)
					builder.append(text);
			}
			parsingProcessor.nextToken();
		}
		if (builder.length() != 0)
			processURL(builder.toString(), parsingProcessor);
	}

	private void processURL(String pageURL, ParsingProcessor parsingProcessor) {
		String faviconURL = strategy.obtainFaviconURL(pageURL, parsingProcessor.getContextRoot());
		pageURLs.add(pageURL);
		faviconURLs.add(faviconURL);
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
