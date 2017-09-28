package cz.gattserver.grass3.articles.basic.style.color;

import java.util.List;

import cz.gattserver.grass3.articles.basic.style.StyleTree;
import cz.gattserver.grass3.articles.parser.interfaces.AbstractElementTree;
import cz.gattserver.grass3.articles.parser.interfaces.Context;

public class GreenTree extends StyleTree {

	public GreenTree(List<AbstractElementTree> elist) {
		super(elist);
	}

	@Override
	public void generateStartTag(Context ctx) {
		ctx.print("<span style='color: green'>");
	}

	@Override
	public void generateEndTag(Context ctx) {
		ctx.print("</span>");
	}

}
