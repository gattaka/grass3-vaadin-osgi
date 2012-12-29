package org.myftp.gattserver.grass.articles.container.plugin;

import java.util.ArrayList;
import java.util.List;

import org.myftp.gattserver.grass.articles.container.plugin.ContainerTree;
import org.myftp.gattserver.grass.articles.lexer.Token;
import org.myftp.gattserver.grass.articles.parser.PluginBag;
import org.myftp.gattserver.grass.articles.parser.exceptions.ParserException;
import org.myftp.gattserver.grass.articles.parser.interfaces.AbstractElementTree;
import org.myftp.gattserver.grass.articles.parser.interfaces.AbstractParserPlugin;


/**
 * 
 * @author gatt
 */
public class ContainerElement extends AbstractParserPlugin {

	private String tag;

	public ContainerElement(String tag) {
		this.tag = tag;
	}

	/**
	 * Zpracovat počáteční tag
	 */
	private void acceptStartTag(PluginBag pluginBag) {

		String startTag = pluginBag.getStartTag();
		if (!startTag.equals(tag)) {
			log("Čekal jsem: [" + tag + "], ne " + startTag);
			throw new ParserException();
		}

		/**
		 * START_TAG byl zpracován - načíst další token
		 */
		pluginBag.nextToken();

	}

	/**
	 * zpracovat koncový tag
	 */
	private void acceptEndTag(PluginBag pluginBag) {

		String endTag = pluginBag.getEndTag();
		if (!endTag.equals(tag)) {
			log("Čekal jsem: [/" + tag + "], ne " + pluginBag.getCode());
			throw new ParserException();
		}

		/**
		 * END_TAG byl zpracován - načíst další token
		 */
		pluginBag.nextToken();

	}

	public AbstractElementTree parse(PluginBag pluginBag) {

		acceptStartTag(pluginBag);

		// zkus načíst první element - pokud je to odřádkování, tak ho ignoruj
		if (pluginBag.getToken().equals(Token.EOL))
			pluginBag.nextToken();

		List<AbstractElementTree> elist = new ArrayList<AbstractElementTree>();
		pluginBag.getBlock(elist); 

		acceptEndTag(pluginBag); 

		// zkus načíst element za mnou - pokud je to odřádkování, tak ho ignoruj (aby to nedělalo mezery)
		if (pluginBag.getToken().equals(Token.EOL))
			pluginBag.nextToken();

		return new ContainerTree(elist);
	}

	public boolean canHoldBreakline() {
		// TODO
		return false;
	}
}
