package cz.gattserver.grass3.articles.plugins.basic.style;

import java.util.List;

import cz.gattserver.grass3.articles.editor.parser.Context;
import cz.gattserver.grass3.articles.editor.parser.elements.Element;

public class SupElement extends AbstractStyleElement {

	public SupElement(List<Element> elist) {
		super(elist);
	}

	@Override
	public void generateStartTag(Context ctx) {
		ctx.print("<sup>");
	}

	@Override
	public void generateEndTag(Context ctx) {
		ctx.print("</sup>");
	}

}
