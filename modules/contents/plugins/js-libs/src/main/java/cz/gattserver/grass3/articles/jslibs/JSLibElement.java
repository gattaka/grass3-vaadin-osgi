package cz.gattserver.grass3.articles.jslibs;

import java.util.List;

import cz.gattserver.grass3.articles.editor.parser.Context;
import cz.gattserver.grass3.articles.editor.parser.elements.Element;

public class JSLibElement implements Element {

	private String scriptPath;

	public JSLibElement(String scriptPath) {
		this.scriptPath = scriptPath;
	}

	@Override
	public void apply(Context ctx) {
		ctx.addJSResource(scriptPath);
	}

	@Override
	public List<Element> getSubElements() {
		return null;
	}
}
