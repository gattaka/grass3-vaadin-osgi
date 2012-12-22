package org.myftp.gattserver.grass3.articles.basic.abbr;

import org.myftp.gattserver.grass3.articles.parser.interfaces.AbstractElementTree;
import org.myftp.gattserver.grass3.articles.parser.interfaces.IContext;

public class AbbrTree extends AbstractElementTree {

	private String text;
	private String title;

	public AbbrTree(String text, String title) {
		this.text = text;
		this.title = title;
	}

	@Override
	public void generateElement(IContext ctx) {
		ctx.print("<abbr title=\"" + title + "\">" + text + "</abbr>");
	}
}
