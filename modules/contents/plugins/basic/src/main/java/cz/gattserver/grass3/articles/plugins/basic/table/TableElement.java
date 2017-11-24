package cz.gattserver.grass3.articles.plugins.basic.table;

import java.util.Iterator;
import java.util.List;

import cz.gattserver.grass3.articles.editor.parser.Context;
import cz.gattserver.grass3.articles.editor.parser.elements.Element;

/**
 * @author Hynek
 */
public class TableElement implements Element {

	private List<List<Element>> listElements;
	private boolean withHead;
	private int cols;

	public TableElement(List<List<Element>> listElements, boolean withHead, int cols) {
		this.listElements = listElements;
		this.withHead = withHead;
		this.cols = cols;
	}

	@Override
	public void apply(Context ctx) {
		Iterator<List<Element>> iterator = listElements.iterator();
		List<Element> tableElement = iterator.next();

		ctx.print(
				"<table bordercolor=\"#888\" cellpadding=\"5px\" rules=\"all\" border=\"0\" style=\"margin-left: auto; margin-right: auto;\">");
		if (withHead) {
			ctx.print("<thead>");
			boolean lineComplete = false;
			for (int i = 0; i < cols; i++) {
				ctx.print("<th>");
				lineComplete = tableElement.isEmpty();
				if (lineComplete == false) {
					// vypiš všechny elementy pole tabulky
					for (Element elementTree : tableElement) {
						elementTree.apply(ctx);
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
					for (Element element : tableElement) {
						element.apply(ctx);
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
