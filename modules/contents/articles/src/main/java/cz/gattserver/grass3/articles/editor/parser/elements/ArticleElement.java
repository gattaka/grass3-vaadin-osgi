package cz.gattserver.grass3.articles.editor.parser.elements;

import java.util.List;

import cz.gattserver.grass3.articles.editor.parser.Context;

/**
 * @author gatt
 */
public class ArticleElement implements Element {

	private List<Element> children;

	public ArticleElement(List<Element> elements) {
		this.children = elements;
	}

	@Override
	public void apply(Context ctx) {
		if (children != null) {
			for (Element et : children) 
				et.apply(ctx);
		} else {
			ctx.print("~ empty ~");
		}
	}
}
