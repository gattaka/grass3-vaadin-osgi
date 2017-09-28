package cz.gattserver.grass3.articles.basic.style;

import java.util.List;

import cz.gattserver.grass3.articles.parser.interfaces.AbstractElementTree;
import cz.gattserver.grass3.articles.parser.interfaces.Context;

public class MonospaceTree extends StyleTree {

	public MonospaceTree(List<AbstractElementTree> elist) {
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
