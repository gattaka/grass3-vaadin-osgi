package cz.gattserver.grass3.articles.plugins.basic.style;

import java.util.List;

import cz.gattserver.grass3.articles.editor.parser.Context;
import cz.gattserver.grass3.articles.editor.parser.elements.Element;

public class SubElement extends AbstractStyleElement {

	public SubElement(List<Element> elist) {
		super(elist);
	}

	@Override
	public void generateStartTag(Context ctx) {
		ctx.print("<sub>");
	}

	@Override
	public void generateEndTag(Context ctx) {
		ctx.print("</sub>");
	}

}
