package cz.gattserver.grass3.articles.parser.elements;

import cz.gattserver.grass3.articles.parser.interfaces.AbstractElementTree;
import cz.gattserver.grass3.articles.parser.interfaces.IContext;

public class BreaklineTree extends AbstractElementTree {

    private String text = "<br/>";

    public BreaklineTree(boolean canHoldBreakline) {
        if (canHoldBreakline == false) {
            this.text = "\n";
        }
    }

    public void generateElement(IContext ctx) {
        ctx.println(text);
    }
}
