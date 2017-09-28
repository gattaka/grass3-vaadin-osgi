package cz.gattserver.grass3.articles.basic.abbr;

import cz.gattserver.grass3.articles.parser.interfaces.AbstractElementTree;
import cz.gattserver.grass3.articles.parser.interfaces.Context;

public class AbbrTree extends AbstractElementTree {

	private String text;
	private String title;

	public AbbrTree(String text, String title) {
		this.text = text;
		this.title = title;
	}

	@Override
	public void generateElement(Context ctx) {
		ctx.print("<abbr title=\"" + title + "\">" + text + "</abbr>");
	}
}
