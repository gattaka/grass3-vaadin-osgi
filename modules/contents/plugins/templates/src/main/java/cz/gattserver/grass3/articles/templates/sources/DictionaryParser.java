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

/**
 * @author gatt
 */
public class DictionaryParser implements Parser {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	private String tag;
	private List<String> dictionary = new ArrayList<>();

	public DictionaryParser(String tag) {
		this.tag = tag;
	}

	@Override
	public Element parse(ParsingProcessor pluginBag) {
		// zpracovat počáteční tag
		parseStartTag(pluginBag);

		// zpracuje zdroje
		parseSources(pluginBag);

		// zpracovat koncový tag
		parseEndTag(pluginBag);
		return new DictionaryElement(dictionary);
	}

	private void parseStartTag(ParsingProcessor pluginBag) {
		String startTag = pluginBag.getStartTag();

		if (!startTag.equals(tag)) {
			logger.warn("Čekal jsem: [{}] ne {}", tag, startTag);
			throw new ParserException();
		}

		pluginBag.nextToken();
	}

	private void parseSources(ParsingProcessor parsingProcessor) {
		// dokud není konec souboru nebo můj uzavírací tag
		StringBuilder builder = new StringBuilder();
		while (parsingProcessor.getToken() != Token.EOF
				&& (parsingProcessor.getToken() != Token.END_TAG || !parsingProcessor.getEndTag().equals(tag))) {
			if (parsingProcessor.getToken() == Token.EOL && builder.length() != 0) {
				dictionary.add(builder.toString());
				builder = new StringBuilder();
			} else {
				String text = parsingProcessor.getText();
				if (text != null && !text.isEmpty())
					builder.append(text);
			}
			parsingProcessor.nextToken();
		}
		if (builder.length() != 0)
			dictionary.add(builder.toString());

		dictionary.sort((s1, s2) -> s1.toLowerCase().compareTo(s2.toLowerCase()));
	}

	private void parseEndTag(ParsingProcessor pluginBag) {
		String endTag = pluginBag.getEndTag();

		if (!endTag.equals(tag)) {
			logger.warn("Čekal jsem: [/{}] ne {}", tag, pluginBag.getCode());
			throw new ParserException();
		}

		pluginBag.nextToken();
	}

}
