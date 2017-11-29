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
public class SourcesParser implements Parser {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	private String tag;
	private List<String> sources = new ArrayList<>();

	public SourcesParser(String tag) {
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
		return new SourcesElement(sources, pluginBag.getContextRoot());
	}

	@Override
	public boolean canHoldBreakline() {
		// nemůžu vložit <br/> do <a></a> elementu
		return false;
	}

	private void parseStartTag(ParsingProcessor pluginBag) {
		String startTag = pluginBag.getStartTag();

		if (!startTag.equals(tag)) {
			logger.warn("Čekal jsem: [" + tag + "] ne " + startTag);
			throw new ParserException();
		}

		pluginBag.nextToken();
	}

	private void parseSources(ParsingProcessor pluginBag) {
		// dokud není konec souboru nebo můj uzavírací tag
		StringBuilder builder = new StringBuilder();
		while (pluginBag.getToken() != Token.EOF
				&& (pluginBag.getToken() != Token.END_TAG || pluginBag.getEndTag().equals(tag) == false)) {
			if (pluginBag.getToken() == Token.EOL && builder.length() != 0) {
				sources.add(builder.toString());
				builder = new StringBuilder();
			} else {
				String text = pluginBag.getText();
				if (text != null && text.isEmpty() == false)
					builder.append(text);
			}
			pluginBag.nextToken();
		}
		if (builder.length() != 0)
			sources.add(builder.toString());
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
