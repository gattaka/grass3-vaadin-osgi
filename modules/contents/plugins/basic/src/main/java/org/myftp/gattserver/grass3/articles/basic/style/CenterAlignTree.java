package org.myftp.gattserver.grass3.articles.basic.style;

import java.util.List;

import org.myftp.gattserver.grass3.articles.parser.interfaces.AbstractElementTree;
import org.myftp.gattserver.grass3.articles.parser.interfaces.IContext;


public class CenterAlignTree extends StyleTree {

	public CenterAlignTree(List<AbstractElementTree> elist) {
		super(elist);
	}

	@Override
	public void generateStartTag(IContext ctx) {
		ctx.print("<div style='text-align: center'>");
	}

	@Override
	public void generateEndTag(IContext ctx) {
		ctx.print("</div>");
	}
}
