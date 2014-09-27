package cz.gattserver.grass3.articles.basic.image;

import cz.gattserver.grass3.articles.lexer.Token;
import cz.gattserver.grass3.articles.parser.PluginBag;
import cz.gattserver.grass3.articles.parser.exceptions.ParserException;
import cz.gattserver.grass3.articles.parser.interfaces.AbstractElementTree;
import cz.gattserver.grass3.articles.parser.interfaces.AbstractParserPlugin;


/**
 * 
 * @author gatt
 */
public class ImageElement extends AbstractParserPlugin {

	private String tag;

	public ImageElement(String tag) {
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
		StringBuilder link = new StringBuilder();
		while ((pluginBag.getToken() != Token.END_TAG || !pluginBag.getEndTag().equals(tag)) && pluginBag.getToken() != Token.EOF ) {
			link.append(pluginBag.getTextTree().getText());
		}

		// zpracovat koncový tag
		String endTag = pluginBag.getEndTag();
		log(this.getClass().getSimpleName() + ": " + pluginBag.getToken());

		if (!endTag.equals(tag)) {
			log("Čekal jsem: %s, ne %s%n", "[/" + tag + ']', pluginBag.getCode());
			throw new ParserException();
		}

		// END_TAG byl zpracován
		pluginBag.nextToken();

		return new ImageTree(link.toString());
	}

	@Override
	public boolean canHoldBreakline() {
		// nemůžu vložit <br/> do <img/> elementu
		return false;
	}

}
