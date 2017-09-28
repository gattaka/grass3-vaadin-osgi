package cz.gattserver.grass3.articles.container.plugin;

import java.util.List;

import cz.gattserver.grass3.articles.parser.interfaces.AbstractElementTree;
import cz.gattserver.grass3.articles.parser.interfaces.Context;


public class ContainerTree extends AbstractElementTree {

	private List<AbstractElementTree> elist;

	public ContainerTree(List<AbstractElementTree> elist) {
		this.elist = elist;
	}

	public void generateElement(Context ctx) {
		
		ctx.addCSSResource("articles/container/style.css");
		ctx.addJSResource("articles/container/container.js");

		ctx.print("<div class=\"container_box\">");
		ctx.print("<div class=\"container_switch\" onclick=\"toggle_container(this);\"></div>");
		ctx.print("<div class=\"container_content\">");

		for (AbstractElementTree tree : elist) {
			tree.generate(ctx);
		}
		
		ctx.print("<div class=\"container_end_switch\" onclick=\"toggle_container();\"></div>");
		ctx.print("</div>");
		ctx.print("</div>"); 
	}
}
