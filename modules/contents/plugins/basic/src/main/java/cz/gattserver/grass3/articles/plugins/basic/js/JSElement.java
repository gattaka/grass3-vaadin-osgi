package cz.gattserver.grass3.articles.plugins.basic.js;

import cz.gattserver.grass3.articles.editor.parser.Context;
import cz.gattserver.grass3.articles.editor.parser.elements.Element;

public class JSElement implements Element {

	private String content;

	public JSElement(String content) {
		this.content = content;
	}

	@Override
	public void apply(Context ctx) {
		ctx.addJSCode(content);
	}

}
