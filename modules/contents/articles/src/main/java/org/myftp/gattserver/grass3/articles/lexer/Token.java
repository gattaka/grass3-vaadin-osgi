/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.myftp.gattserver.grass3.articles.lexer;

/**
 *
 * @author gatt
 */
public enum Token {
    START_TAG,      // počáteční tag
    END_TAG,        // koncový tag
    TEXT,           // text
    EOL,            // konec řádku
    EOF             // konec souboru
}
