package cz.gattserver.grass3.articles.basic.style;

import java.util.List;

import cz.gattserver.grass3.articles.parser.interfaces.AbstractElementTree;
import cz.gattserver.grass3.articles.parser.interfaces.IContext;


public class StrongTree extends StyleTree {

	public StrongTree(List<AbstractElementTree> elist) {
		super(elist);
	}

	@Override
	public void generateStartTag(IContext ctx) {
		ctx.print("<strong>");
	}

	@Override
	public void generateEndTag(IContext ctx) {
		ctx.print("</strong>");
	}

}
