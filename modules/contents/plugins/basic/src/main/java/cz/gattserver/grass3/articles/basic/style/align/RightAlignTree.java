package cz.gattserver.grass3.articles.basic.style.align;

import java.util.List;

import cz.gattserver.grass3.articles.basic.style.StyleTree;
import cz.gattserver.grass3.articles.parser.interfaces.AbstractElementTree;
import cz.gattserver.grass3.articles.parser.interfaces.Context;


public class RightAlignTree extends StyleTree {

	public RightAlignTree(List<AbstractElementTree> elist) {
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
