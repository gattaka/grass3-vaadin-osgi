package cz.gattserver.grass3.articles.plugins.headers;

import java.util.List;

import cz.gattserver.grass3.articles.editor.parser.Context;
import cz.gattserver.grass3.articles.editor.parser.elements.Element;

public class HeaderElement implements Element {

	private List<Element> headerContent;
	private int level;

	public HeaderElement(List<Element> headerContent, int level) {
		this.headerContent = headerContent;
		this.level = level;
	}

	@Override
	public void apply(Context ctx) {
		ctx.resetHeaderLevel();
		ctx.print("<div class=\"articles-h" + level + "\">");
		for (Element headerText : headerContent)
			headerText.apply(ctx);
		ctx.print(" <a class=\"articles-h-id\" href=\"" + ctx.getNextHeaderIdentifier() + "\"></a>");
		ctx.print("</div>");
		ctx.setHeaderLevel(level);
		ctx.addCSSResource("articles/style.css");
	}
	
	@Override
	public List<Element> getSubElements() {
		return headerContent;
	}

}
