package org.myftp.gattserver.grass3.articles.basic.templates.sources;

import java.util.ArrayList;
import java.util.List;

import org.myftp.gattserver.grass3.articles.basic.headers.HeaderTree;
import org.myftp.gattserver.grass3.articles.favlink.plugin.LinkTree;
import org.myftp.gattserver.grass3.articles.parser.elements.TextTree;
import org.myftp.gattserver.grass3.articles.parser.interfaces.AbstractElementTree;
import org.myftp.gattserver.grass3.articles.parser.interfaces.IContext;

public class SourcesTree extends AbstractElementTree {

	private List<String> sources;
	private String contextRoot;

	public SourcesTree(List<String> sources, String contextRoot) {
		this.sources = sources;
		this.contextRoot = contextRoot;
	}

	@Override
	public void generateElement(IContext ctx) {
		List<AbstractElementTree> headerList = new ArrayList<>();
		headerList.add(new TextTree("Odkazy a zdroje"));

		// zapiš nadpis
		new HeaderTree(headerList, 1).generateElement(ctx);

		ctx.print("<ol style=\"padding-left: 25px; margin-top: 0px;\" >");
		for (String source : sources) {
			ctx.print("<li>");
			String[] chunks = source.split("\\s+");
			for (int i = 0; i < chunks.length; i++) {
				if (i != chunks.length - 1) {
					ctx.print(chunks[i]);
					ctx.print(" ");
				} else {
					new LinkTree(chunks[chunks.length - 1], contextRoot).generateElement(ctx);
				}
			}
			ctx.print("</li>");
		}
		ctx.print("</ol>");
	}
}
