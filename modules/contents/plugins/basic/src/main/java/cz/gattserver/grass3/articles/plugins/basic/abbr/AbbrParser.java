package cz.gattserver.grass3.articles.plugins.basic.abbr;

import cz.gattserver.grass3.articles.editor.lexer.Token;
import cz.gattserver.grass3.articles.editor.parser.Parser;
import cz.gattserver.grass3.articles.editor.parser.ParsingProcessor;
import cz.gattserver.grass3.articles.editor.parser.elements.Element;
import cz.gattserver.grass3.articles.editor.parser.exceptions.ParserException;
import cz.gattserver.grass3.articles.editor.parser.exceptions.TokenException;

/**
 * @author gatt
 */
public class AbbrParser implements Parser {

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
		if (!startTag.equals(tag))
			throw new TokenException(tag, startTag);
		pluginBag.nextToken();
	}

	private void parseAbbreviation(ParsingProcessor pluginBag) {
		if (pluginBag.getToken() != Token.EOF)
			text = pluginBag.getText();
		else
			throw new ParserException();
		pluginBag.nextToken();
	}

	private void parseTextStartTag(ParsingProcessor pluginBag) {
		String startTag = pluginBag.getStartTag();
		if (!startTag.equals(titleTag))
			throw new TokenException(titleTag, startTag);
		pluginBag.nextToken();
	}

	private void parseTitle(ParsingProcessor pluginBag) {
		if (pluginBag.getToken() != Token.EOF)
			title = pluginBag.getText();
		else
			throw new TokenException(Token.TEXT);
		pluginBag.nextToken();
	}

	private void parseTextEndTag(ParsingProcessor pluginBag) {
		String endTag = pluginBag.getEndTag();
		if (!endTag.equals(titleTag))
			throw new TokenException(titleTag, pluginBag.getCode());
		pluginBag.nextToken();
	}

	private void parseEndTag(ParsingProcessor pluginBag) {
		String endTag = pluginBag.getEndTag();
		if (!endTag.equals(tag))
			throw new TokenException(tag, pluginBag.getCode());
		pluginBag.nextToken();
	}
}
