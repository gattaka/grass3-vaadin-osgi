
package org.myftp.gattserver.grass3.articles.parser.interfaces;

/**
 *
 * @author gatt
 */
public abstract class AbstractParserPlugin extends AbstractParser {

    /** ParserPlugin
     *
     *  - zajišťuje dědění funkcionality z Parser
     *  - dělá některé společné věci pluginů (jinak by pluginy mohly dědit
     *    přímo od Parser
     *
     *  - potřebuje pluginBag, protože v něm jsou předávané informace
     */

    /**
     * Zjistí od pluginu jestli bere konce řádků
     * 
     * @return true pokud je povolen v pluginu "<br/>"
     */
    public abstract boolean canHoldBreakline();
    
}
