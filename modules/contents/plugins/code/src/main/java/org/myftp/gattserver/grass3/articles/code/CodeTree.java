package org.myftp.gattserver.grass3.articles.code;

import org.myftp.gattserver.grass3.articles.parser.interfaces.AbstractElementTree;
import org.myftp.gattserver.grass3.articles.parser.interfaces.IContext;

public class CodeTree extends AbstractElementTree {

	private String code;
	private String style;
	private int lines;
	private String description;

	private HighlightEngine highlightEngine;

	public CodeTree(String code, String style, int lines, String description,
			HighlightEngine highlightEngine) {
		this.code = code;
		this.style = style;
		this.lines = lines;
		this.description = description;
		this.highlightEngine = highlightEngine;
	}

	public void generateElement(IContext ctx) {

		String element = "";
		String name = "";

		switch (highlightEngine) {
		case SHJS:
			ctx.addCSSResource("articles/code/sh_style.css");
			ctx.addJSResource("articles/code/js/sh_main.js");
			element = "pre";
			break;
		case CODEMIRROR:
			ctx.addCSSResource("articles/code/codemirror.css");
			ctx.addJSResource("articles/code/js/codemirror.js");
			element = "textarea";
			name = "name=\"" + style + "\"";
			break;
		}

		if (style != null && !style.isEmpty())
			ctx.addJSResource("articles/code/js/lang/" + style + ".js");

		ctx.print("<span class=\"lang_description\">" + description + "</span>");
		if (highlightEngine == HighlightEngine.SHJS) {
			ctx.print("<table class=\"numbertable\">");
			boolean odd = true;
			for (int i = 1; i <= lines; i++) {
				ctx.print("<tr><td style=\"background-color:"
						+ (odd ? "#f0eada" : "#f3efdd") + "\">" + i
						+ "</td></tr>");
				odd = odd ? false : true;
			}
			ctx.print("</table>");
		}
		ctx.print("<div class=\"barier\"><div class=\"numberedtext\">");
		ctx.print("<" + element + " class=\"" + style + "\" " + name + ">"
				+ code + "</" + element + ">");
		ctx.print("</div></div><div id=\"code_koncovka\"></div>");
	}

	@Override
	public String toString() {
		return "Code highlight";
	}

}
