package cz.gattserver.grass3.articles.templates.sources;

import java.util.ArrayList;
import java.util.List;

import cz.gattserver.grass3.articles.editor.parser.Context;
import cz.gattserver.grass3.articles.editor.parser.elements.BreaklineElement;
import cz.gattserver.grass3.articles.editor.parser.elements.Element;
import cz.gattserver.grass3.articles.editor.parser.elements.TextElement;
import cz.gattserver.grass3.articles.plugins.headers.HeaderElement;

public class DictionaryElement implements Element {

	private static final String DELIMITTER = " - ";
	private List<String> dictionary;

	public DictionaryElement(List<String> dictionary) {
		this.dictionary = dictionary;
	}

	@Override
	public void apply(Context ctx) {
		ctx.addCSSResource("articles/templates/style.css");

		char lastLetter = 0;
		boolean start = true;
		for (String entry : dictionary) {
			if (lastLetter == 0 || entry.toLowerCase().charAt(0) != lastLetter) {
				if (!start)
					new BreaklineElement().apply(ctx);
				lastLetter = entry.toLowerCase().charAt(0);
				List<Element> headerList = new ArrayList<>();
				headerList.add(new TextElement(String.valueOf(lastLetter).toUpperCase()));
				new HeaderElement(headerList, 1).apply(ctx);
			}
			int index = entry.indexOf(DELIMITTER);
			ctx.print("<div class='articles-dictionary-entry'>");
			if (index > 0) {
				ctx.print("<div><strong>");
				ctx.print(entry.substring(0, index));
				ctx.print("</strong></div>");
				ctx.print("<div>&nbsp;&mdash;&nbsp;</div>");
				ctx.print("<div>");
				ctx.print(entry.substring(index + DELIMITTER.length()));
				ctx.print("</div>");
			} else {
				ctx.print(entry);
			}
			ctx.print("</div>");
			start = false;
		}
	}
}
