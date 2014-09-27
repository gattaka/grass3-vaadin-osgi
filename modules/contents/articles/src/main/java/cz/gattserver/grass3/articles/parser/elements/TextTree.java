package cz.gattserver.grass3.articles.parser.elements;

import cz.gattserver.grass3.articles.parser.interfaces.AbstractElementTree;
import cz.gattserver.grass3.articles.parser.interfaces.IContext;

public class TextTree extends AbstractElementTree {

    private String text;

    public TextTree(String text) {
        this.text = text;
    }

    public void generateElement(IContext ctx) {
        ctx.println(text);
    }

	public String getText() {
		return text;
	}
}
