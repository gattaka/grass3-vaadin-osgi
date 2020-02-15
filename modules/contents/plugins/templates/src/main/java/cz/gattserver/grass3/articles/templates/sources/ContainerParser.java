package cz.gattserver.grass3.articles.templates.sources;

import java.util.ArrayList;
import java.util.List;

import cz.gattserver.grass3.articles.editor.lexer.Token;
import cz.gattserver.grass3.articles.editor.parser.Parser;
import cz.gattserver.grass3.articles.editor.parser.ParsingProcessor;
import cz.gattserver.grass3.articles.editor.parser.elements.Element;

/**
 * @author gatt
 */
public class ContainerParser implements Parser {

	private String tag;

	public ContainerParser(String tag) {
		this.tag = tag;
	}

	@Override
	public Element parse(ParsingProcessor pluginBag) {
		pluginBag.getStartTag();
		pluginBag.nextToken();

		List<Element> elist = new ArrayList<>();
		pluginBag.getBlock(elist, tag);

		pluginBag.getEndTag();

		pluginBag.nextToken();

		// protože za elementem je mezera ignoruje se případný <br/>
		if (pluginBag.getToken().equals(Token.EOL))
			pluginBag.nextToken();

		return new ContainerElement(elist);
	}

}
