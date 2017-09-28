package cz.gattserver.grass3.articles.basic.image;

import cz.gattserver.grass3.articles.parser.interfaces.AbstractElementTree;
import cz.gattserver.grass3.articles.parser.interfaces.Context;

public class ImageTree extends AbstractElementTree {

	private String link;

	public ImageTree(String link) {
		this.link = link;
	}

	@Override
	public void generateElement(Context ctx) {
		ctx.print("<a target=\"_blank\" href=\"" + link + "\"><img class=\"articles-basic-img\" src=\"" + link + "\" alt=\"" + link
				+ "\" /></a>");
	}

}
