package org.myftp.gattserver.grass3.articles.favlink.plugin;

import org.myftp.gattserver.grass3.articles.lexer.Token;
import org.myftp.gattserver.grass3.articles.parser.PluginBag;
import org.myftp.gattserver.grass3.articles.parser.exceptions.ParserException;
import org.myftp.gattserver.grass3.articles.parser.interfaces.AbstractElementTree;
import org.myftp.gattserver.grass3.articles.parser.interfaces.AbstractParserPlugin;

/**
 * 
 * @author gatt
 */
public class LinkElement extends AbstractParserPlugin {

	private String tag;

	public LinkElement(String tag) {
		this.tag = tag;
	}

	public AbstractElementTree parse(PluginBag pluginBag) {

		// zpracovat počáteční tag
		String startTag = pluginBag.getStartTag();

		if (!startTag.equals(tag)) {
			log("Čekal jsem: [" + tag + "] ne " + startTag);
			throw new ParserException();
		}

		// START_TAG byl zpracován
		pluginBag.nextToken();

		StringBuilder link = new StringBuilder();

		// zpracovat text - musím zahazovat anotace pozic, střetly by se
		while ((pluginBag.getToken() != Token.END_TAG || !pluginBag.getEndTag()
				.equals(tag)) && pluginBag.getToken() != Token.EOF) {
			link.append(pluginBag.getText());
			pluginBag.nextToken();
		}

		// zpracovat koncový tag
		String endTag = pluginBag.getEndTag();

		if (!endTag.equals(tag)) {
			log("Čekal jsem: [/" + tag + "] ne " + pluginBag.getCode());
			throw new ParserException();
		}

		// END_TAG byl zpracován
		pluginBag.nextToken();

		return new LinkTree(link.toString());
	}

	@Override
	public boolean canHoldBreakline() {
		// nemůžu vložit <br/> do <a></a> elementu
		return false;
	}
}
