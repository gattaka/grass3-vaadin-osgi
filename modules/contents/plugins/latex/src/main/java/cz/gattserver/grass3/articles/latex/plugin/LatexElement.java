package cz.gattserver.grass3.articles.latex.plugin;

import cz.gattserver.grass3.articles.editor.parser.Context;
import cz.gattserver.grass3.articles.editor.parser.elements.Element;

public class LatexElement implements Element {

	private String path;
	private String source;

	public LatexElement(String path, String source) {
		this.path = path;
		this.source = source;
	}

	@Override
	public void apply(Context ctx) {
		ctx.print("<img style=\"vertical-align: middle;\" src=\"" + path + "\" alt=\"latex rovnice\" title=\"" + source
				+ "\">");
	}

	@Override
	public String toString() {
		return String.format("LaTeX %s", path.toString());
	}

}
