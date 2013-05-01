package org.myftp.gattserver.grass3.articles.basic.style.align;

import java.util.List;

import org.myftp.gattserver.grass3.articles.basic.style.StyleTree;
import org.myftp.gattserver.grass3.articles.parser.interfaces.AbstractElementTree;
import org.myftp.gattserver.grass3.articles.parser.interfaces.IContext;


public class RightAlignTree extends StyleTree {

	public RightAlignTree(List<AbstractElementTree> elist) {
		super(elist);
	}

	@Override
	public void generateStartTag(IContext ctx) {
		ctx.print("<div style='text-align: right'>");
	}

	@Override
	public void generateEndTag(IContext ctx) {
		ctx.print("</div>");
	}
}
