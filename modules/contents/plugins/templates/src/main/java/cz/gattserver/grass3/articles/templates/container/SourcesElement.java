package cz.gattserver.grass3.articles.templates.container;

import java.util.ArrayList;
import java.util.List;

import cz.gattserver.grass3.articles.editor.parser.Context;
import cz.gattserver.grass3.articles.editor.parser.elements.Element;
import cz.gattserver.grass3.articles.editor.parser.elements.TextElement;
import cz.gattserver.grass3.articles.plugins.favlink.plugin.FavlinkElement;
import cz.gattserver.grass3.articles.plugins.headers.HeaderElement;

public class SourcesElement implements Element {

	private List<String> faviconURLs = new ArrayList<>();
	private List<String> descriptions = new ArrayList<>();
	private List<String> pageURLs = new ArrayList<>();
	private boolean header;
	private boolean numbers;

	public SourcesElement(List<String> faviconURLs, List<String> descriptions, List<String> pageURLs, boolean header,
			boolean numbers) {
		this.faviconURLs = faviconURLs;
		this.descriptions = descriptions;
		this.pageURLs = pageURLs;
		this.header = header;
		this.numbers = numbers;
	}

	@Override
	public void apply(Context ctx) {
		if (header) {
			List<Element> headerList = new ArrayList<>();
			headerList.add(new TextElement("Odkazy a zdroje"));
			new HeaderElement(headerList, 1).apply(ctx);
		}

		String element = numbers ? "ol" : "ul";
		ctx.print("<" + element + " style=\"padding-left: 25px; margin-top: 0px;\" >");
		for (int i = 0; i < faviconURLs.size(); i++) {
			ctx.print("<li>");
			new FavlinkElement(faviconURLs.get(i), descriptions.get(i), pageURLs.get(i)).apply(ctx);
			ctx.print("</li>");
		}
		ctx.print("</" + element + ">");
	}
}
