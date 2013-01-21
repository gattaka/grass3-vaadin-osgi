package org.myftp.gattserver.grass3.articles.basic.table;

import java.util.Iterator;
import java.util.List;

import org.myftp.gattserver.grass3.articles.parser.interfaces.AbstractElementTree;
import org.myftp.gattserver.grass3.articles.parser.interfaces.IContext;


public class TableTree extends AbstractElementTree {

	private List<List<AbstractElementTree>> listElements;
	private boolean withHead;
	private int cols;

	public TableTree(List<List<AbstractElementTree>> listElements, boolean withHead, int cols) {
		this.listElements = listElements;
		this.withHead = withHead;
		this.cols = cols;
	}

	@Override
	public void generateElement(IContext ctx) {
		Iterator<List<AbstractElementTree>> iterator = listElements.iterator();
		List<AbstractElementTree> tableElement = iterator.next();

		ctx.print("<table bordercolor=\"#888\" cellpadding=\"5px\" rules=\"all\" border=\"0\" style=\"margin-left: auto; margin-right: auto;\">");
		if (withHead) {
			ctx.print("<thead>");
			boolean lineComplete = false;
			for (int i = 0; i < cols; i++) {
				ctx.print("<th>");
				lineComplete = tableElement.isEmpty();
				if (lineComplete == false) {
					// vypiš všechny elementy pole tabulky
					for (AbstractElementTree elementTree : tableElement) {
						elementTree.generate(ctx);
					}
					tableElement = iterator.next();
				}
				ctx.print("</th>");
			}
			ctx.print("</thead>");
		}
		/**
		 * Tabulka musí kontrolovat prázdná okna, ta totiž znamenají konec řádky
		 * tabulky, přičemž každá řádka může mít různý počet políček - maximálně
		 * cols
		 */
		while (true) {
			ctx.print("<tr>");
			boolean lineComplete = false;
			for (int i = 0; i < cols; i++) {
				ctx.print("<td>");				
				if (tableElement.isEmpty()) {
					if (i > 0)
						lineComplete = true;
					else if (iterator.hasNext())
						tableElement = iterator.next();
					else
						iterator = null;
				}
				if (iterator != null && lineComplete == false) {
					// vypiš všechny elementy pole tabulky
					for (AbstractElementTree element : tableElement) {
						element.generate(ctx);
					}
					if (iterator.hasNext())
						tableElement = iterator.next();
					else
						iterator = null;
				}
				ctx.print("</td>");
			}
			ctx.print("</tr>");
			if (iterator == null)
				break;
		}
		ctx.print("</table>");
	}
}
