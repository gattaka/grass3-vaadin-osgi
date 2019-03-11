package cz.gattserver.grass3.articles.plugins.favlink.plugin;

import cz.gattserver.grass3.articles.editor.parser.Context;
import cz.gattserver.grass3.articles.editor.parser.elements.Element;

public class FavlinkElement implements Element {

	private static final int MAX_LENGTH = 90;

	private String link = null;
	private String imgURL = null;

	public FavlinkElement(String faviconURL, String pageURL) {
		this.imgURL = faviconURL;
		this.link = pageURL;
	}

	private String createShortLink(String link) {
		if (link.length() <= MAX_LENGTH)
			return link;
		return link.substring(0, MAX_LENGTH / 2 - 3) + "..." + link.substring(link.length() - MAX_LENGTH / 2);
	}

	@Override
	public void apply(Context ctx) {
		ctx.print("<a style=\"word-wrap: break-word\" href=\"" + link + "\" >");
		if (imgURL != null)
			ctx.print("<img style=\"margin: 4px 5px -4px 2px;\" height=\"16\" width=\"16\" src=\"" + imgURL + "\" />");
		ctx.print(createShortLink(link));
		ctx.print("</a>");
	}
}
