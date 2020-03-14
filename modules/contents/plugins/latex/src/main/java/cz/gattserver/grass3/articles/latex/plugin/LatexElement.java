package cz.gattserver.grass3.articles.latex.plugin;

import java.util.UUID;

import cz.gattserver.grass3.articles.editor.parser.Context;
import cz.gattserver.grass3.articles.editor.parser.elements.Element;

public class LatexElement implements Element {

	private String formula;

	public LatexElement(String formula) {
		this.formula = formula;
	}

	@Override
	public void apply(Context ctx) {
		// CSS resources
		ctx.addCSSResource("articles/katex/katex.min.css");
		
		// JS resources
		ctx.addJSResource("articles/katex/katex.min.js");
		
		String uuid = "katex" + UUID.randomUUID().toString();
		ctx.print("<span id=\"" + uuid + "\"></span>");
		
		ctx.addJSCode("katex.render(\"" + formula + "\", document.getElementById(\"" + uuid + "\"), {throwOnError: false});");		
	}

	@Override
	public String toString() {
		return String.format("LaTeX %s", formula);
	}

}
