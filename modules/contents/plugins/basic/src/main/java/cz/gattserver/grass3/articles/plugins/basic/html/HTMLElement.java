package cz.gattserver.grass3.articles.plugins.basic.html;

import cz.gattserver.grass3.articles.editor.parser.Context;
import cz.gattserver.grass3.articles.editor.parser.elements.Element;

public class HTMLElement implements Element {

	private String content;

	public HTMLElement(String content) {
		this.content = content;
	}

	@Override
	public void apply(Context ctx) {
		ctx.print("<div id=\"htmldiv\">" + content + "</div>");
	}

}
