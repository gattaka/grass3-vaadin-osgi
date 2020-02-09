package cz.gattserver.grass3.articles.plugins.basic.style.align;

import java.util.List;

import cz.gattserver.grass3.articles.editor.parser.Context;
import cz.gattserver.grass3.articles.editor.parser.elements.Element;
import cz.gattserver.grass3.articles.plugins.basic.style.AbstractStyleElement;

public class RightAlignElement extends AbstractStyleElement {

	public RightAlignElement(List<Element> elist) {
		super(elist);
	}

	@Override
	public void generateStartTag(Context ctx) {
		ctx.print("<div style='text-align: right'>");
	}

	@Override
	public void generateEndTag(Context ctx) {
		ctx.print("</div>");
	}
}
