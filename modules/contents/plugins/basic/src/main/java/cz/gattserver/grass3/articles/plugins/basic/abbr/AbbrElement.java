package cz.gattserver.grass3.articles.plugins.basic.abbr;

import java.util.List;

import cz.gattserver.grass3.articles.editor.parser.Context;
import cz.gattserver.grass3.articles.editor.parser.elements.Element;

public class AbbrElement implements Element {

	private String text;
	private String title;

	public AbbrElement(String text, String title) {
		this.text = text;
		this.title = title;
	}

	@Override
	public void apply(Context ctx) {
		ctx.print("<abbr title=\"" + title + "\">" + text + "</abbr>");
	}

	@Override
	public List<Element> getSubElements() {
		return null;
	}
}
