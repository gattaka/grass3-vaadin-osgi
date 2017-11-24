package cz.gattserver.grass3.articles.editor.parser.elements;

import cz.gattserver.grass3.articles.editor.parser.Context;

public class BreaklineElement implements Element {

	private String text = "<br/>";

	public BreaklineElement(boolean canHoldBreakline) {
		if (canHoldBreakline == false) {
			this.text = "\n";
		}
	}

	@Override
	public void apply(Context ctx) {
		ctx.println(text);
	}
}
