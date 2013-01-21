package org.myftp.gattserver.grass3.articles.basic.style;

import java.util.List;

import org.myftp.gattserver.grass3.articles.parser.interfaces.AbstractElementTree;
import org.myftp.gattserver.grass3.articles.parser.interfaces.IContext;


public class ItalicTree extends StyleTree {

    public ItalicTree(List<AbstractElementTree> elist) {
        super(elist);
    }

    @Override
    public void generateStartTag(IContext ctx) {
        ctx.print("<em>");
    }

    @Override
    public void generateEndTag(IContext ctx) {
        ctx.print("</em>");
    }

}
