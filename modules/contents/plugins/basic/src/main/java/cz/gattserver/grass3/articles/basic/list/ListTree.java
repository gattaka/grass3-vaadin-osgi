package cz.gattserver.grass3.articles.basic.list;

import java.util.List;

import cz.gattserver.grass3.articles.parser.interfaces.AbstractElementTree;
import cz.gattserver.grass3.articles.parser.interfaces.IContext;


public class ListTree extends AbstractElementTree {

	private List<List<AbstractElementTree>> listElements;
	private boolean ordered;

	public ListTree(List<List<AbstractElementTree>> listElements, boolean ordered) {
		this.listElements = listElements;
		this.ordered = ordered;
	}

	@Override
	public void generateElement(IContext ctx) {
		ctx.print(ordered ? "<ol>" : "<ul>");
		/**
		 * Každá položka listu se může skládat z více parserových elementů
		 */
		for (List<AbstractElementTree> elist : listElements) {
			ctx.print("<li>");
			for (AbstractElementTree elementTree : elist) {
				elementTree.generate(ctx);
			}
			ctx.print("</li>");
		}
		ctx.print(ordered ? "</ol>" : "</ul>");
	}
}
