package cz.gattserver.grass3.articles.basic.style;

import java.util.List;

import cz.gattserver.grass3.articles.parser.interfaces.AbstractElementTree;
import cz.gattserver.grass3.articles.parser.interfaces.IContext;


public abstract class StyleTree extends AbstractElementTree {

    private List<AbstractElementTree> elements;

    public StyleTree(List<AbstractElementTree> elements) {
        this.elements = elements;
    }

    @Override
    public void generateElement(IContext ctx) {
        generateStartTag(ctx);
        generateBlock(ctx);
        generateEndTag(ctx);
    }

    protected abstract void generateStartTag(IContext ctx);

    protected abstract void generateEndTag(IContext ctx);

    protected void generateBlock(IContext ctx) {
        if (elements != null) {
            for (AbstractElementTree et : elements) {
                et.generate(ctx);
            }
        }
    }
}
