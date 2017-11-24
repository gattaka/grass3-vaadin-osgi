package cz.gattserver.grass3.articles.editor.parser.util;

/**
 * Jednoduchá implementace cyklického pole pro hledání řetězce elementu nadpisu
 * 
 * @author Gattaka
 * 
 */
public class FinderArray {

	/**
	 * Jak dlouhé je potřeba cyklické pole pro hledání oddělovače částí ?
	 * Aktuálně postačí 4, protože nadpis je označován jako '[Nx]', kde x je z
	 * {0,9}
	 */
	private static final int SEARCH_BUFFER_LENGTH = 4;
	private char[] searchBuffer = new char[SEARCH_BUFFER_LENGTH];
	private int startPointer = 0;

	public void addChar(char c) {
		/**
		 * Vždy se zapisuje na "poslední" pozici, která je současně starou první
		 * (aby startPointer ukazoval na počátek SEARCH_BUFFER_LENGTH-dlouhé
		 * sekvence)
		 */
		searchBuffer[startPointer] = c;
		startPointer = (startPointer + 1) % SEARCH_BUFFER_LENGTH;
	}

	public char getChar(int index) {
		return searchBuffer[(startPointer + index) % SEARCH_BUFFER_LENGTH];
	}

	public int getSize() {
		return SEARCH_BUFFER_LENGTH;
	}

}