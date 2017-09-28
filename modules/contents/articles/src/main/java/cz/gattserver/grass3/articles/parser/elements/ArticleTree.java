/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.gattserver.grass3.articles.parser.elements;

import java.util.List;

import cz.gattserver.grass3.articles.parser.interfaces.AbstractElementTree;
import cz.gattserver.grass3.articles.parser.interfaces.Context;


/**
 *
 * @author gatt
 */
public class ArticleTree extends AbstractElementTree {

    private List<AbstractElementTree> elements;

    public ArticleTree(List<AbstractElementTree> elements) {
        this.elements = elements;
    }

    public void generateElement(Context ctx) {
        if (elements != null) {
            for (AbstractElementTree et : elements) {
                et.generate(ctx);
            }
        } else {
            ctx.print("~ empty ~");
        }
    }
}
