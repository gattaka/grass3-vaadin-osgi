package cz.gattserver.grass3.articles.templates.sources;

import java.util.ArrayList;
import java.util.List;

import cz.gattserver.grass3.articles.editor.parser.Context;
import cz.gattserver.grass3.articles.editor.parser.elements.Element;
import cz.gattserver.grass3.articles.editor.parser.elements.TextElement;
import cz.gattserver.grass3.articles.plugins.favlink.plugin.FavlinkElement;
import cz.gattserver.grass3.articles.plugins.headers.HeaderElement;

public class SourcesElement implements Element {

	private List<String> sources;
	private String contextRoot;

	public SourcesElement(List<String> sources, String contextRoot) {
		this.sources = sources;
		this.contextRoot = contextRoot;
	}

	@Override
	public void apply(Context ctx) {
		List<Element> headerList = new ArrayList<>();
		headerList.add(new TextElement("Odkazy a zdroje"));

		// zapiš nadpis
		new HeaderElement(headerList, 1).apply(ctx);

		ctx.print("<ol style=\"padding-left: 25px; margin-top: 0px;\" >");
		for (String source : sources) {
			ctx.print("<li>");
			String[] chunks = source.split("\\s+");
			for (int i = 0; i < chunks.length; i++) {
				if (i != chunks.length - 1) {
					ctx.print(chunks[i]);
					ctx.print(" ");
				} else {
					new FavlinkElement(chunks[chunks.length - 1], contextRoot).apply(ctx);
				}
			}
			ctx.print("</li>");
		}
		ctx.print("</ol>");
	}
}
