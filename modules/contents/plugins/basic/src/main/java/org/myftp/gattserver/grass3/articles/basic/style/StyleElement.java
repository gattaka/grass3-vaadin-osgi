package org.myftp.gattserver.grass3.articles.basic.style;


import java.util.ArrayList;
import java.util.List;

import org.myftp.gattserver.grass3.articles.parser.PluginBag;
import org.myftp.gattserver.grass3.articles.parser.exceptions.ParserException;
import org.myftp.gattserver.grass3.articles.parser.interfaces.AbstractElementTree;
import org.myftp.gattserver.grass3.articles.parser.interfaces.AbstractParserPlugin;

/**
 * 
 * @author gatt
 */
public abstract class StyleElement extends AbstractParserPlugin {

	private String tag;

	public StyleElement(String tag) {
		this.tag = tag;
	}

	public AbstractElementTree parse(PluginBag pluginBag) {


		// zpracovat počáteční tag
		String startTag = pluginBag.getStartTag();
		log(this.getClass().getSimpleName() + ": " + pluginBag.getToken());

		if (!startTag.equals(tag)) {
			log("Čekal jsem: %s, ne %s%n", '[' + tag + ']', startTag);
			throw new ParserException();
		}

		// START_TAG byl zpracován
		pluginBag.nextToken();

		// zpracovat text
		// tady se sice pustí blok, ale blok nemá jinou zarážku než EOF,
		// to já nechci - já potřebuju aby skončil na definovaném tagu
		List<AbstractElementTree> elist = new ArrayList<AbstractElementTree>();
		pluginBag.getBlock(elist);
		// nextToken() - je již voláno v block() !!!

		// zpracovat koncový tag
		String endTag = pluginBag.getEndTag();
		log(this.getClass().getSimpleName() + ": " + pluginBag.getToken());

		if (!endTag.equals(tag)) {
			log("Čekal jsem: %s, ne %s%n", "[/" + tag + ']', endTag);
			throw new ParserException();
		}

		// END_TAG byl zpracován
		pluginBag.nextToken();

		return getTree(elist);
	}

	protected abstract StyleTree getTree(List<AbstractElementTree> elist);

	@Override
	public boolean canHoldBreakline() {
		return true;
	}
}
