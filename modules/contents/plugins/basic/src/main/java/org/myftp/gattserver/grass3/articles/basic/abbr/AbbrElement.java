package org.myftp.gattserver.grass3.articles.basic.abbr;

import org.myftp.gattserver.grass3.articles.lexer.Token;
import org.myftp.gattserver.grass3.articles.parser.PluginBag;
import org.myftp.gattserver.grass3.articles.parser.exceptions.ParserException;
import org.myftp.gattserver.grass3.articles.parser.interfaces.AbstractElementTree;
import org.myftp.gattserver.grass3.articles.parser.interfaces.AbstractParserPlugin;


/**
 * 
 * @author gatt
 */
public class AbbrElement extends AbstractParserPlugin {

	private String tag;
	private String titleTag;
	private String title;
	private String text;

	public AbbrElement(String tag, String titleTag) {
		this.tag = tag;
		this.titleTag = titleTag;
	}

	private void parseStartTag(PluginBag pluginBag) {
		String startTag = pluginBag.getStartTag();

		if (!startTag.equals(tag)) {
			log("Čekal jsem: [" + tag + "] ne " + startTag);
			throw new ParserException();
		}

		pluginBag.nextToken();
	}

	private void parseAbbreviation(PluginBag pluginBag) {
		if (pluginBag.getToken() != Token.EOF) {
			text = pluginBag.getText();
		}

		if (text == null)
			throw new ParserException();
		
		pluginBag.nextToken();
	}

	private void parseTextStartTag(PluginBag pluginBag) {
		String startTag = pluginBag.getStartTag();

		if (!startTag.equals(titleTag)) {
			log("Čekal jsem: [" + titleTag + "] ne " + startTag);
			throw new ParserException();
		}

		pluginBag.nextToken();
	}

	private void parseTitle(PluginBag pluginBag) {
		if (pluginBag.getToken() != Token.EOF) {
			title = pluginBag.getText();
		}

		if (title == null)
			throw new ParserException();
		
		pluginBag.nextToken();
	}

	private void parseTextEndTag(PluginBag pluginBag) {
		String endTag = pluginBag.getEndTag();

		if (!endTag.equals(titleTag)) {
			log("Čekal jsem: [/" + titleTag + "] ne " + pluginBag.getCode());
			throw new ParserException();
		}

		pluginBag.nextToken();
	}

	private void parseEndTag(PluginBag pluginBag) {
		String endTag = pluginBag.getEndTag();

		if (!endTag.equals(tag)) {
			log("Čekal jsem: [/" + tag + "] ne " + pluginBag.getCode());
			throw new ParserException();
		}

		pluginBag.nextToken();
	}

	public AbstractElementTree parse(PluginBag pluginBag) {

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

		return new AbbrTree(text, title);
	}

	@Override
	public boolean canHoldBreakline() {
		// nemůžu vložit <br/> do <a></a> elementu
		return false;
	}
}
