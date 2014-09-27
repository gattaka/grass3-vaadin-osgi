package cz.gattserver.grass3.articles.basic.style.color;

import java.util.List;

import cz.gattserver.grass3.articles.basic.style.StyleTree;
import cz.gattserver.grass3.articles.parser.interfaces.AbstractElementTree;
import cz.gattserver.grass3.articles.parser.interfaces.IContext;


public class BlueTree extends StyleTree {

	public BlueTree(List<AbstractElementTree> elist) {
		super(elist);
	}

	@Override
	public void generateStartTag(IContext ctx) {
		ctx.print("<span style='color: blue'>");
	}

	@Override
	public void generateEndTag(IContext ctx) {
		ctx.print("</span>");
	}

}
