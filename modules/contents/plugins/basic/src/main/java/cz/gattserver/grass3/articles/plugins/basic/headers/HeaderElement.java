package cz.gattserver.grass3.articles.plugins.basic.headers;

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
		ctx.print("<div class=\"articles-basic-h" + level + "\">");
		ctx.print("<a class=\"articles-basic-h-id\" href=\"" + ctx.getNextHeaderIdentifier() + "\"></a>");
		for (Element headerText : headerContent)
			headerText.apply(ctx);
		ctx.print("</div>");
		ctx.setHeaderLevel(level);
		ctx.addCSSResource("articles/basic/style.css");
	}

	public void setHeaderText(List<Element> headerContent) {
		this.headerContent = headerContent;
	}

	public List<Element> getHeaderText() {
		return headerContent;
	}

}
