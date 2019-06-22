package cz.gattserver.grass3.articles.plugins.basic.style;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.gattserver.grass3.articles.editor.lexer.Token;
import cz.gattserver.grass3.articles.editor.parser.Parser;
import cz.gattserver.grass3.articles.editor.parser.ParsingProcessor;
import cz.gattserver.grass3.articles.editor.parser.elements.Element;
import cz.gattserver.grass3.articles.editor.parser.exceptions.TokenException;

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
	public Element parse(ParsingProcessor pluginBag) {

		// zpracovat počáteční tag
		String startTag = pluginBag.getStartTag();
		logger.debug("{}", pluginBag.getToken());

		if (!startTag.equals(tag))
			throw new TokenException(tag, startTag);

		// START_TAG byl zpracován
		pluginBag.nextToken();

		// zpracovat text
		// tady se sice pustí blok, ale blok nemá jinou zarážku než EOF,
		// to já nechci - já potřebuju aby skončil na definovaném tagu
		List<Element> elist = new ArrayList<>();
		pluginBag.getBlock(elist, tag);

		// zpracovat koncový tag, není potřeba kontrolovat, jaký je to endtag,
		// protože buď je to můj endtag nebo jde o EOF (vyplývá z bloku)
		pluginBag.getEndTag();
		logger.debug("{}", pluginBag.getToken());
	
		// END_TAG byl zpracován
		pluginBag.nextToken();

		// Zkus za stylem zpracovat ještě konec řádku
		if (pluginBag.getToken().equals(Token.EOL))
			pluginBag.nextToken();
		
		return getElement(elist);
	}
}
