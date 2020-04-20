package cz.gattserver.grass3.articles.jslibs.colors;

import java.util.List;

import cz.gattserver.grass3.articles.editor.parser.Context;
import cz.gattserver.grass3.articles.editor.parser.elements.Element;

public class GJSLibMatrixElement implements Element {

	@Override
	public void apply(Context ctx) {
		ctx.addJSResource("articles/jslibs/js/matrix.js");
	}
	
	@Override
	public List<Element> getSubElements() {
		return null;
	}
}
