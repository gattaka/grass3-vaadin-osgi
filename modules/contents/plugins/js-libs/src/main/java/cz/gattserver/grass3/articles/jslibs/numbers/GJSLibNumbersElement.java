package cz.gattserver.grass3.articles.jslibs.numbers;

import java.util.List;

import cz.gattserver.grass3.articles.editor.parser.Context;
import cz.gattserver.grass3.articles.editor.parser.elements.Element;

public class GJSLibNumbersElement implements Element {

	@Override
	public void apply(Context ctx) {
		ctx.addJSResource("articles/jslibs/js/numbers.js");
	}

	@Override
	public List<Element> getSubElements() {
		return null;
	}
}
