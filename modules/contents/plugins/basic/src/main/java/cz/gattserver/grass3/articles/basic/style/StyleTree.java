package cz.gattserver.grass3.articles.basic.style;

import java.util.List;

import cz.gattserver.grass3.articles.parser.interfaces.AbstractElementTree;
import cz.gattserver.grass3.articles.parser.interfaces.Context;


public abstract class StyleTree extends AbstractElementTree {

    private List<AbstractElementTree> elements;

    public StyleTree(List<AbstractElementTree> elements) {
        this.elements = elements;
    }

    @Override
    public void generateElement(Context ctx) {
        generateStartTag(ctx);
        generateBlock(ctx);
        generateEndTag(ctx);
    }

    protected abstract void generateStartTag(Context ctx);

    protected abstract void generateEndTag(Context ctx);

    protected void generateBlock(Context ctx) {
        if (elements != null) {
            for (AbstractElementTree et : elements) {
                et.generate(ctx);
            }
        }
    }
}
