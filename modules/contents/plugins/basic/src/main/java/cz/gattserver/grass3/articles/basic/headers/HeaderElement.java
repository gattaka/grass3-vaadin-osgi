package cz.gattserver.grass3.articles.basic.headers;

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
public class HeaderElement extends AbstractParserPlugin {

	private int level;
	private String tag;

	public HeaderElement(int level, String tag) {
		this.level = level;
		this.tag = tag;
	}

	public AbstractElementTree parse(PluginBag pluginBag) {

		// zpracovat počáteční tag
		String startTag = pluginBag.getStartTag();
		log(this.getClass().getSimpleName() + ": " + pluginBag.getToken());

		if (!startTag.equals(tag)) {
			log("Čekal jsem: [" + tag + "], ne [" + startTag + "]");
			throw new ParserException();
		}

		// START_tag byl zpracován
		pluginBag.nextToken();

		/*
		 * // elementy .. prostě blok List<EditorElementTree> elist = new
		 * ArrayList<EditorElementTree>(); block(elist);
		 */

		// zpracovat text
		List<AbstractElementTree> elist = new ArrayList<AbstractElementTree>();
		pluginBag.getBlock(elist);
		// nextToken() - je již voláno v text() !!!

		// zpracovat koncový tag
		String endTag = pluginBag.getEndTag();
		if (!endTag.equals(tag)) {
			log("Čekal jsem: [/" + tag + "], ne [" + endTag + "]");
			throw new ParserException();
		}

		// END_tag byl zpracován
		pluginBag.nextToken();

		// protože za H je mezera ignoruje se případný <br/>
		if (pluginBag.getToken().equals(Token.EOL)) {
			pluginBag.nextToken();
		}

		return new HeaderTree(elist, level);
	}

	/**
	 * http://validator.w3.org/#validate_by_input+with_options
	 * http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd
	 * 
	 */
	@Override
	public boolean canHoldBreakline() {
		// nemůžu vložit <br/> do <h1></h1> elementu
		return false;
	}

}
