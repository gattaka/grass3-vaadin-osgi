package cz.gattserver.grass3.articles.basic.list;

import java.util.ArrayList;
import java.util.List;

import cz.gattserver.grass3.articles.lexer.Token;
import cz.gattserver.grass3.articles.parser.PluginBag;
import cz.gattserver.grass3.articles.parser.exceptions.ParserException;
import cz.gattserver.grass3.articles.parser.interfaces.AbstractElementTree;
import cz.gattserver.grass3.articles.parser.interfaces.AbstractParserPlugin;


/**
 * 
 * @author gatt
 */
public class ListElement extends AbstractParserPlugin {

	private String tag;
	private boolean ordered;

	public ListElement(String tag, boolean ordered) {
		this.ordered = ordered;
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

		/**
		 * Zpracovat položek listu, nemůžu volat blok, protože ten končí až na
		 * mém koncovém tagu, kdežto já potřebuju odlišit položky listu konci
		 * řádků
		 */
		List<List<AbstractElementTree>> listElements = new ArrayList<List<AbstractElementTree>>();
		List<AbstractElementTree> elist = new ArrayList<AbstractElementTree>();
		while ((pluginBag.getToken() != Token.EOF)
				&& (pluginBag.getToken() != Token.END_TAG || !pluginBag.getEndTag().equals(tag))) {
			switch (pluginBag.getToken()) {
			/**
			 * V elementu listu můžou být jiné pluginy
			 */
			case START_TAG:
				elist.add(pluginBag.getElement());
				break;
			/**
			 * Jinak to načítám jako text
			 */
			case END_TAG:
			case TEXT:
				elist.add(pluginBag.getTextTree());
				break;
			/**
			 * Konec řádku značí konec položky listu
			 */
			case EOL:
				listElements.add(elist);
				elist = new ArrayList<AbstractElementTree>();
				pluginBag.nextToken();
				break;
			default:
				break;
			}
		}

		// po ukončení může být ještě něco nepřidáno - přidej to
		if (!elist.isEmpty())
			listElements.add(elist);

		// zpracovat koncový tag
		String endTag = pluginBag.getEndTag();

		if (!endTag.equals(tag)) {
			log("Čekal jsem: [/" + tag + "] ne " + pluginBag.getCode());
			throw new ParserException();
		}

		// END_TAG byl zpracován
		pluginBag.nextToken();

		// protože za listem je mezera ignoruje se případný <br/>
		if (pluginBag.getToken().equals(Token.EOL)) {
			pluginBag.nextToken();
		}

		return new ListTree(listElements, ordered);
	}

	@Override
	public boolean canHoldBreakline() {
		// nemůžu vložit <br/> do <a></a> elementu
		return false;
	}
}
