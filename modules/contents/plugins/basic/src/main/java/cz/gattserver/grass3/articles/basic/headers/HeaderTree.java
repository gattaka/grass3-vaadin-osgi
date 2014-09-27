package cz.gattserver.grass3.articles.basic.headers;

import java.util.List;

import cz.gattserver.grass3.articles.parser.interfaces.AbstractElementTree;
import cz.gattserver.grass3.articles.parser.interfaces.IContext;

public class HeaderTree extends AbstractElementTree {

	private List<AbstractElementTree> headerContent;
	private int level;

	public HeaderTree(List<AbstractElementTree> headerContent, int level) {
		this.headerContent = headerContent;
		this.level = level;
	}

	public void setHeaderText(List<AbstractElementTree> headerContent) {
		this.headerContent = headerContent;
	}

	public List<AbstractElementTree> getHeaderText() {
		return headerContent;
	}

	@Override
	public void generateElement(IContext ctx) {
		ctx.resetHeaderLevel();
		ctx.print("<div class=\"articles-basic-h" + level + "\">");
		ctx.print("<a class=\"articles-basic-h-id\" href=\""
				+ ctx.getNextHeaderIdentifier() + "\"></a>");
		for (AbstractElementTree headerText : headerContent)
			headerText.generate(ctx);
		ctx.print("</div>");
		ctx.setHeaderLevel(level);
		ctx.addCSSResource("articles/basic/style.css");
	}

}
