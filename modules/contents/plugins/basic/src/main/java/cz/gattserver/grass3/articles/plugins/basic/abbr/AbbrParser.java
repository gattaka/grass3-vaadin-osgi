package cz.gattserver.grass3.articles.plugins.basic.abbr;

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
public class AbbrParser implements Parser {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	private String tag;
	private String titleTag;
	private String title;
	private String text;

	public AbbrParser(String tag, String titleTag) {
		this.tag = tag;
		this.titleTag = titleTag;
	}

	@Override
	public Element parse(ParsingProcessor pluginBag) {
		// zpracovat počáteční tag
		parseStartTag(pluginBag);

		// zpracuje zkratku (text)
		parseAbbreviation(pluginBag);

		// zpracovat počáteční tag textu
		parseTextStartTag(pluginBag);

		// zpracuje title (popis, vysvětlení zkratky)
		parseTitle(pluginBag);

		// zpracuje koncový tag textu
		parseTextEndTag(pluginBag);

		// zpracovat koncový tag
		parseEndTag(pluginBag);

		return new AbbrElement(text, title);
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

	private void parseAbbreviation(ParsingProcessor pluginBag) {
		if (pluginBag.getToken() != Token.EOF) {
			text = pluginBag.getText();
		}

		if (text == null)
			throw new ParserException();

		pluginBag.nextToken();
	}

	private void parseTextStartTag(ParsingProcessor pluginBag) {
		String startTag = pluginBag.getStartTag();

		if (!startTag.equals(titleTag)) {
			logger.warn("Čekal jsem: [" + titleTag + "] ne " + startTag);
			throw new ParserException();
		}

		pluginBag.nextToken();
	}

	private void parseTitle(ParsingProcessor pluginBag) {
		if (pluginBag.getToken() != Token.EOF) {
			title = pluginBag.getText();
		}

		if (title == null)
			throw new ParserException();

		pluginBag.nextToken();
	}

	private void parseTextEndTag(ParsingProcessor pluginBag) {
		String endTag = pluginBag.getEndTag();

		if (!endTag.equals(titleTag)) {
			logger.warn("Čekal jsem: [/" + titleTag + "] ne " + pluginBag.getCode());
			throw new ParserException();
		}

		pluginBag.nextToken();
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
