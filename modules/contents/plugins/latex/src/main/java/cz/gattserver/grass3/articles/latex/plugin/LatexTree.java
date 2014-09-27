package cz.gattserver.grass3.articles.latex.plugin;

import cz.gattserver.grass3.articles.parser.interfaces.AbstractElementTree;
import cz.gattserver.grass3.articles.parser.interfaces.IContext;

public class LatexTree extends AbstractElementTree {

	private String path;
	private String source;

	public LatexTree(String path, String source) {
		this.path = path;
		this.source = source;
	}

	@Override
	public void generateElement(IContext ctx) {
		ctx.print("<img style=\"vertical-align: middle;\" src=\"" + path + "\" alt=\"latex rovnice\" title=\"" + source + "\">");
	}

	@Override
	public String toString() {
		return String.format("LaTeX %s", path.toString());
	}

}
