package org.myftp.gattserver.grass3.articles.code;

import org.myftp.gattserver.grass3.articles.parser.interfaces.AbstractElementTree;
import org.myftp.gattserver.grass3.articles.parser.interfaces.IContext;

public class CodeTree extends AbstractElementTree {

	private String code;
	private String style;
	private String description;

	private String[] libs;

	public CodeTree(String code, String description, String style,
			String... libs) {
		this.code = code;
		this.description = description;
		this.style = style;
		this.libs = libs;
	}

	public void generateElement(IContext ctx) {

		// CSS resources
		ctx.addCSSResource("articles/code/code_style.css");
		ctx.addCSSResource("articles/code/codemirror.css");

		// JS resources
		ctx.addJSResource("articles/code/js/codemirror.js");
		ctx.addJSResource("articles/code/js/matchbrackets.js");
		ctx.addJSResource("articles/code/js/xml-fold.js");
		ctx.addJSResource("articles/code/js/matchtags.js");
		ctx.addJSResource("articles/code/js/active-line.js");

		for (String lib : libs)
			ctx.addJSResource("articles/code/js/lang/" + lib);
		ctx.addJSResource("articles/code/js/lang/" + style + ".js"); 
		
		ctx.print("<span class=\"lang_description\">" + description + "</span>");
		ctx.print("<div class=\"barier\"><div class=\"numberedtext\">");
		ctx.print("<textarea name=\"" + style + "\">" + code + "</textarea>");
		ctx.print("</div></div><div id=\"code_koncovka\"></div>");
	}

	@Override
	public String toString() {
		return "Code highlight";
	}

}
