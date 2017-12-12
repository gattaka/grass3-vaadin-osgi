package cz.gattserver.grass3.articles.templates.sources;

import java.util.ArrayList;
import java.util.List;

import cz.gattserver.grass3.articles.editor.parser.Context;
import cz.gattserver.grass3.articles.editor.parser.elements.Element;
import cz.gattserver.grass3.articles.editor.parser.elements.TextElement;
import cz.gattserver.grass3.articles.plugins.favlink.plugin.FavlinkElement;
import cz.gattserver.grass3.articles.plugins.headers.HeaderElement;

public class SourcesElement implements Element {

	private List<String> faviconURLs = new ArrayList<>();
	private List<String> pageURLs = new ArrayList<>();

	public SourcesElement(List<String> faviconURLs, List<String> pageURLs) {
		this.faviconURLs = faviconURLs;
		this.pageURLs = pageURLs;
	}

	@Override
	public void apply(Context ctx) {
		List<Element> headerList = new ArrayList<>();
		headerList.add(new TextElement("Odkazy a zdroje"));

		// zapiš nadpis
		new HeaderElement(headerList, 1).apply(ctx);

		ctx.print("<ol style=\"padding-left: 25px; margin-top: 0px;\" >");
		for (int i = 0; i < faviconURLs.size(); i++) {
			ctx.print("<li>");
			new FavlinkElement(faviconURLs.get(i), pageURLs.get(i)).apply(ctx);
			ctx.print("</li>");
		}
		ctx.print("</ol>");
	}
}
