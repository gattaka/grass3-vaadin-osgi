package org.myftp.gattserver.grass3.articles.code;

import org.myftp.gattserver.grass3.articles.parser.interfaces.AbstractElementTree;
import org.myftp.gattserver.grass3.articles.parser.interfaces.IContext;

public class CodeTree extends AbstractElementTree {

	private String code;
	private String style;
	private int lines;
	private String description;

	public CodeTree(String code, String style, int lines, String description) {
		this.code = code;
		this.style = style;
		this.lines = lines;
		this.description = description;
	}

	public void generateElement(IContext ctx) {

		ctx.addCSSResource("articles/code/sh_style.css");

		if (style != null && !style.isEmpty())
			ctx.addJSResource("articles/code/js/lang/" + style + ".js");

		ctx.addJSResource("articles/code/js/sh_main.js");

		ctx.print("<span class=\"lang_description\">" + description + "</span>");
		ctx.print("<table class=\"numbertable\">");
		boolean odd = true;
		for (int i = 1; i <= lines; i++) {
			ctx.print("<tr><td style=\"background-color:"
					+ (odd ? "#f0eada" : "#f3efdd") + "\">" + i + "</td></tr>");
			odd = odd ? false : true;
		}
		ctx.print("</table>");
		ctx.print("<div class=\"barier\"><div class=\"numberedtext\">");
		ctx.print("<pre class=\"" + style + "\">" + code + "</pre>");
		ctx.print("</div></div><div id=\"code_koncovka\"></div>");
	}

	@Override
	public String toString() {
		return "Code heighlight";
	}

}
