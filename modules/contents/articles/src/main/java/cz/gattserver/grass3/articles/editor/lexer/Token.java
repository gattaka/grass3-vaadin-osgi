package cz.gattserver.grass3.articles.editor.lexer;

/**
 * @author gatt
 */
public enum Token {

	/**
	 * Počáteční tag
	 */
	START_TAG,

	/**
	 * Koncový tag
	 */
	END_TAG,

	/**
	 * Text
	 */
	TEXT,

	/**
	 * Konec řádku
	 */
	EOL,

	/**
	 * Konec souboru
	 */
	EOF
}
