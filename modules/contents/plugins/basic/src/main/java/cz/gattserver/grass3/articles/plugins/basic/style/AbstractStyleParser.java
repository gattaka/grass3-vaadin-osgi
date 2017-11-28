package cz.gattserver.grass3.articles.plugins.basic.style;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.gattserver.grass3.articles.editor.parser.Parser;
import cz.gattserver.grass3.articles.editor.parser.PluginBag;
import cz.gattserver.grass3.articles.editor.parser.elements.Element;
import cz.gattserver.grass3.articles.editor.parser.exceptions.ParserException;

/**
 * @author gatt
 */
public abstract class AbstractStyleParser implements Parser {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	private String tag;

	protected abstract AbstractStyleElement getElement(List<Element> elist);

	public AbstractStyleParser(String tag) {
		this.tag = tag;
	}

	@Override
	public Element parse(PluginBag pluginBag) {

		// zpracovat počáteční tag
		String startTag = pluginBag.getStartTag();
		logger.debug(pluginBag.getToken().toString());

		if (!startTag.equals(tag)) {
			logger.warn("Čekal jsem: %s, ne %s%n", '[' + tag + ']', startTag);
			throw new ParserException();
		}

		// START_TAG byl zpracován
		pluginBag.nextToken();

		// zpracovat text
		// tady se sice pustí blok, ale blok nemá jinou zarážku než EOF,
		// to já nechci - já potřebuju aby skončil na definovaném tagu
		List<Element> elist = new ArrayList<Element>();
		pluginBag.getBlock(elist);
		// nextToken() - je již voláno v block() !!!

		// zpracovat koncový tag
		String endTag = pluginBag.getEndTag();
		logger.debug(pluginBag.getToken().toString());

		if (!endTag.equals(tag)) {
			logger.warn("Čekal jsem: %s, ne %s%n", "[/" + tag + ']', endTag);
			throw new ParserException();
		}

		// END_TAG byl zpracován
		pluginBag.nextToken();

		return getElement(elist);
	}

	@Override
	public boolean canHoldBreakline() {
		return true;
	}
}