package org.myftp.gattserver.grass3.articles.parser.elements;

import org.myftp.gattserver.grass3.articles.parser.interfaces.AbstractElementTree;
import org.myftp.gattserver.grass3.articles.parser.interfaces.IContext;

public class ParserError extends AbstractElementTree {

     private String text;

    public ParserError(String pluginName) {
        String prefix = "<span style=\"color: red; border: red dashed 1px; padding: 2px 5px; margin: 2px; font-family: monospace; font-size: 12px; font-weight: normal; font-style: normal; font-variant: normal; background: #ffcc33;\">Plugin \"";
        String suffix = "\" encountered parsing error</span>";
        text = prefix + pluginName + suffix;
    }

    public void generateElement(IContext ctx) {
        ctx.println(text);
    }
}
