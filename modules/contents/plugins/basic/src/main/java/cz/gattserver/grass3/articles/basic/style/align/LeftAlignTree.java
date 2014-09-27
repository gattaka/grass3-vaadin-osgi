package cz.gattserver.grass3.articles.basic.style.align;

import java.util.List;

import cz.gattserver.grass3.articles.basic.style.StyleTree;
import cz.gattserver.grass3.articles.parser.interfaces.AbstractElementTree;
import cz.gattserver.grass3.articles.parser.interfaces.IContext;


public class LeftAlignTree extends StyleTree {

	public LeftAlignTree(List<AbstractElementTree> elist) {
		super(elist);
	}

	@Override
	public void generateStartTag(IContext ctx) {
		ctx.print("<div style='text-align: left'>");
	}

	@Override
	public void generateEndTag(IContext ctx) {
		ctx.print("</div>");
	}
}
