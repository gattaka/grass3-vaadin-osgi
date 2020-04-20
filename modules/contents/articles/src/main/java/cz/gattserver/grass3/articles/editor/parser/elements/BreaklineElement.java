package cz.gattserver.grass3.articles.editor.parser.elements;

import java.util.List;

import cz.gattserver.grass3.articles.editor.parser.Context;

public class BreaklineElement implements Element {

	private String text = "<br/>";

	@Override
	public void apply(Context ctx) {
		ctx.println(text);
	}

	@Override
	public List<Element> getSubElements() {
		return null;
	}
}
