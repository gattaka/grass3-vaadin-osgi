package cz.gattserver.grass3.articles.jslibs.numbers;

import cz.gattserver.grass3.articles.editor.parser.Context;
import cz.gattserver.grass3.articles.editor.parser.elements.Element;

public class GJSLibNumbersElement implements Element {

	@Override
	public void apply(Context ctx) {
		ctx.addJSResource("articles/jslibs/js/numbers.js");
	}
}
