package org.myftp.gattserver.grass3.articles.basic.templates.sources;

import java.util.ArrayList;
import java.util.List;

import org.myftp.gattserver.grass3.articles.lexer.Token;
import org.myftp.gattserver.grass3.articles.parser.PluginBag;
import org.myftp.gattserver.grass3.articles.parser.exceptions.ParserException;
import org.myftp.gattserver.grass3.articles.parser.interfaces.AbstractElementTree;
import org.myftp.gattserver.grass3.articles.parser.interfaces.AbstractParserPlugin;

/**
 * 
 * @author gatt
 */
public class SourcesElement extends AbstractParserPlugin {

	private String tag;
	private List<String> sources = new ArrayList<>();

	public SourcesElement(String tag) {
		this.tag = tag;
	}

	private void parseStartTag(PluginBag pluginBag) {
		String startTag = pluginBag.getStartTag();

		if (!startTag.equals(tag)) {
			log("Čekal jsem: [" + tag + "] ne " + startTag);
			throw new ParserException();
		}

		pluginBag.nextToken();
	}

	private void parseSources(PluginBag pluginBag) {
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

		// zpracuje zdroje
		parseSources(pluginBag);

		// zpracovat koncový tag
		parseEndTag(pluginBag);

		return new SourcesTree(sources, pluginBag.getContextRoot());
	}

	@Override
	public boolean canHoldBreakline() {
		// nemůžu vložit <br/> do <a></a> elementu
		return false;
	}
}
