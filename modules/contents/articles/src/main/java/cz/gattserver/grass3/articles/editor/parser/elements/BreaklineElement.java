package cz.gattserver.grass3.articles.editor.parser.elements;

import cz.gattserver.grass3.articles.editor.parser.Context;

public class BreaklineElement implements Element {

	private String text = "<br/>";

	@Override
	public void apply(Context ctx) {
		ctx.println(text);
	}
}
