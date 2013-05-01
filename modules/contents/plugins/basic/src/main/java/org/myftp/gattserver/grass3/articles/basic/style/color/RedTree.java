package org.myftp.gattserver.grass3.articles.basic.style.color;

import java.util.List;

import org.myftp.gattserver.grass3.articles.basic.style.StyleTree;
import org.myftp.gattserver.grass3.articles.parser.interfaces.AbstractElementTree;
import org.myftp.gattserver.grass3.articles.parser.interfaces.IContext;


public class RedTree extends StyleTree {

	public RedTree(List<AbstractElementTree> elist) {
		super(elist);
	}

	@Override
	public void generateStartTag(IContext ctx) {
		ctx.print("<span style='color: red'>");
	}

	@Override
	public void generateEndTag(IContext ctx) {
		ctx.print("</span>");
	}

}
