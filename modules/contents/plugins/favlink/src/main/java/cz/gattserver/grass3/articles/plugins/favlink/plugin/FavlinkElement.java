package cz.gattserver.grass3.articles.plugins.favlink.plugin;

import cz.gattserver.grass3.articles.editor.parser.Context;
import cz.gattserver.grass3.articles.editor.parser.elements.Element;

public class FavlinkElement implements Element {

	private String link = null;
	private String imgURL = null;

	public FavlinkElement(String faviconURL, String pageURL) {
		this.imgURL = faviconURL;
		this.link = pageURL;
	}

	@Override
	public void apply(Context ctx) {
		ctx.print("<a href=\"" + link + "\" >");
		if (imgURL != null)
			ctx.print("<img style=\"margin: 4px 5px -4px 2px;\" height=\"16\" width=\"16\" src=\"" + imgURL + "\" />");
		ctx.print(link);
		ctx.print("</a>");
	}
}
