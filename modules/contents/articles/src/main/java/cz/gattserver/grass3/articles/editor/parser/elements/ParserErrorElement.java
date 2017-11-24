package cz.gattserver.grass3.articles.editor.parser.elements;

import cz.gattserver.grass3.articles.editor.parser.Context;

public class ParserErrorElement implements Element {

	private String text;

	public ParserErrorElement(String pluginName) {
		String prefix = "<span style=\"color: red; border: red dashed 1px; padding: 2px 5px; margin: 2px; font-family: monospace; font-size: 12px; font-weight: normal; font-style: normal; font-variant: normal; background: #ffcc33;\">Plugin \"";
		String suffix = "\" encountered parsing error</span>";
		text = prefix + pluginName + suffix;
	}

	@Override
	public void apply(Context ctx) {
		ctx.println(text);
	}
}
