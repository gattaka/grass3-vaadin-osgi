package cz.gattserver.grass3.articles.plugins.basic.style;

import java.util.List;

import cz.gattserver.grass3.articles.editor.parser.Context;
import cz.gattserver.grass3.articles.editor.parser.elements.Element;

public class MonospaceElement extends AbstractStyleElement {

	public MonospaceElement(List<Element> elist) {
		super(elist);
	}

	@Override
	public void generateStartTag(Context ctx) {
		ctx.print("<span class=\"articles-basic-monospaced\">");
		ctx.addCSSResource("articles/basic/style.css");
	}

	@Override
	public void generateEndTag(Context ctx) {
		ctx.print("</span>");
	}

}
