/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.myftp.gattserver.grass3.articles.parser.elements;

import java.util.List;

import org.myftp.gattserver.grass3.articles.parser.interfaces.AbstractElementTree;
import org.myftp.gattserver.grass3.articles.parser.interfaces.IContext;


/**
 *
 * @author gatt
 */
public class ArticleTree extends AbstractElementTree {

    private List<AbstractElementTree> elements;

    public ArticleTree(List<AbstractElementTree> elements) {
        this.elements = elements;
    }

    public void generateElement(IContext ctx) {
        if (elements != null) {
            for (AbstractElementTree et : elements) {
                et.generate(ctx);
            }
        } else {
            ctx.print("~ empty ~");
        }
    }
}
