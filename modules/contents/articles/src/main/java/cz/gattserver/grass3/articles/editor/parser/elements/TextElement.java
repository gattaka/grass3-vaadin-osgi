package cz.gattserver.grass3.articles.editor.parser.elements;

import cz.gattserver.grass3.articles.editor.parser.Context;

public class TextElement implements Element {

	private String text;

	public TextElement(String text) {
		this.text = text;
	}

	@Override
	public void apply(Context ctx) {
		ctx.println(text);
	}

	public String getText() {
		return text;
	}
}
