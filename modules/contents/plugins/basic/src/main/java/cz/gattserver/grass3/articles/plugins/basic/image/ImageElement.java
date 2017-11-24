package cz.gattserver.grass3.articles.plugins.basic.image;

import cz.gattserver.grass3.articles.editor.parser.Context;
import cz.gattserver.grass3.articles.editor.parser.elements.Element;

public class ImageElement implements Element {

	private String link;

	public ImageElement(String link) {
		this.link = link;
	}

	@Override
	public void apply(Context ctx) {
		ctx.print("<a target=\"_blank\" href=\"" + link + "\"><img class=\"articles-basic-img\" src=\"" + link
				+ "\" alt=\"" + link + "\" /></a>");
	}

}
