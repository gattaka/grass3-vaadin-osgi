package cz.gattserver.grass3.articles.plugins.basic.style;

import java.util.List;

import cz.gattserver.grass3.articles.editor.parser.Context;
import cz.gattserver.grass3.articles.editor.parser.elements.Element;

public class CrossoutElement extends AbstractStyleElement {

	public CrossoutElement(List<Element> elist) {
		super(elist);
	}

	@Override
	public void generateStartTag(Context ctx) {
		ctx.print("<span style='text-decoration: line-through'>");
	}

	@Override
	public void generateEndTag(Context ctx) {
		ctx.print("</span>");
	}
	
}
