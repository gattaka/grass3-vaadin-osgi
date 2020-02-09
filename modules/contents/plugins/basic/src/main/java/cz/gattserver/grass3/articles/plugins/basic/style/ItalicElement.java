package cz.gattserver.grass3.articles.plugins.basic.style;

import java.util.List;

import cz.gattserver.grass3.articles.editor.parser.Context;
import cz.gattserver.grass3.articles.editor.parser.elements.Element;

public class ItalicElement extends AbstractStyleElement {

	public ItalicElement(List<Element> elist) {
		super(elist);
	}

	@Override
	public void generateStartTag(Context ctx) {
		ctx.print("<em>");
	}

	@Override
	public void generateEndTag(Context ctx) {
		ctx.print("</em>");
	}

}
