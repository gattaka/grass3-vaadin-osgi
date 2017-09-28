package cz.gattserver.grass3.articles.basic.style;

import java.util.List;

import cz.gattserver.grass3.articles.parser.interfaces.AbstractElementTree;
import cz.gattserver.grass3.articles.parser.interfaces.Context;


public class UnderlineTree extends StyleTree {

	public UnderlineTree(List<AbstractElementTree> elist) {
		super(elist);
	}

	@Override
	public void generateStartTag(Context ctx) {
		ctx.print("<span style='text-decoration: underline'>");
	}

	@Override
	public void generateEndTag(Context ctx) {
		ctx.print("</span>");
	}
}
