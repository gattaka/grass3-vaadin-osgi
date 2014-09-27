package cz.gattserver.grass3.articles.basic.style;

import java.util.List;

import cz.gattserver.grass3.articles.parser.interfaces.AbstractElementTree;
import cz.gattserver.grass3.articles.parser.interfaces.IContext;


public class SupTree extends StyleTree {

	public SupTree(List<AbstractElementTree> elist) {
		super(elist);
	}

	@Override
	public void generateStartTag(IContext ctx) {
		ctx.print("<sup>");
	}

	@Override
	public void generateEndTag(IContext ctx) {
		ctx.print("</sup>");
	}

}
