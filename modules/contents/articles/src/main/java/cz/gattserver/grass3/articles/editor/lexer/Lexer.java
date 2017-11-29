package cz.gattserver.grass3.articles.editor.lexer;

import static cz.gattserver.grass3.articles.editor.lexer.Token.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.gattserver.grass3.articles.editor.parser.interfaces.PositionTO;

/**
 * 
 * @author gatt
 */
public class Lexer {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	private String source;
	private int index; // pozice na řádce
	private int ch; // zkoumaný znak
	private int line; // zkoumaná řádka
	private int col; // zkoumaný sloupec
	private boolean br = false; // pokud se právě skočilo přes breakline, musí
								// se s inkrementací col počkat, jinak by byl
								// napřed
	private int pcol, pline; // pozice řádek, sloupec
	private StringBuilder word = new StringBuilder(); // načtený identifikátor

	/**
	 * Vrati pocatecni pozici aktualniho symbolu.
	 */
	public PositionTO getPosition() {
		return new PositionTO(pline, pcol);
	}

	/**
	 * Bere zdrojový text a seká ho na tokeny
	 * 
	 * @param source
	 *            zdrojový text
	 * @param debugOutput
	 */
	public Lexer(String source) {
		this.source = source == null ? "" : source;
		ch = nextChar(); // naber první znak
	}

	/**
	 * Získá začáteční tag bez závorek '[' a ']' - ověřuje nicméně jestli je to
	 * opravdu tag a jestli je počáteční
	 * 
	 * @return text tagu
	 */
	public String getStartTag() {
		// chci jenom text tagu
		if (word.length() > 2 && word.charAt(0) == '[' && word.charAt(word.length() - 1) == ']'
				&& word.charAt(1) != '/') {
			return word.substring(1, word.length() - 1);
		} else {
			return "";
		}
	}

	/**
	 * Získá koncový tag bez závorek '[' a ']' - ověřuje nicméně jestli je to
	 * opravdu tag a jestli je koncový
	 * 
	 * @return text tagu
	 */
	public String getEndTag() {
		// chci jenom text tagu
		if (word.length() > 3 && word.charAt(0) == '[' && word.charAt(word.length() - 1) == ']'
				&& word.charAt(1) == '/') {
			return word.substring(2, word.length() - 1);
		} else {
			return "";
		}
	}

	/**
	 * Získá text. Myšleno jako celý načtený text, který nebyl rozpoznán jako
	 * tag
	 * 
	 * @return text tagu
	 */
	public String getText() {
		return word.toString();
	}

	/**
	 * Přečte a vrátí jeden znak nebo záporné číslo které udává jaká chyba
	 * nastala
	 * 
	 * @return znak pokud vše dopadlo dobře nebo -1 pokud došly znaky a -2 pokud
	 *         je konec řádky
	 */
	private int nextChar() {
		if (index == source.length())
			return -1;
		/**
		 * index se inkrementuje vždy
		 */
		ch = source.charAt(index);
		index++;
		if (ch == '\n' || ch == '\r') {
			if (ch == '\r')
				index++;
			line++;
			col = 1;
			br = true;
			return -2;
		}
		/**
		 * Údaj o sloupci se inkrementuje jenom pokud před ním nebyl konec řádky
		 */
		if (br == true)
			br = false;
		else
			col++;
		return ch;
	}

	/**
	 * Provede jednu iteraci a vrátí token (DEBUG verze - ve skutečnosti akorát
	 * volá metodu {@code readNextToken} která dělá skutečnou práci; tato metoda
	 * je tady protože readNextToken vrací hodnotu z více míst, tak aby se to
	 * dalo jednoduše logovat ...)
	 * 
	 * @return token, která našel
	 * @see Token
	 */
	public Token nextToken() {
		Token symbol = readNextToken();
		logger.debug("LEXER: Token made -> " + symbol);
		return symbol;
	}

	private Token readNextToken() {

		// ulož si pozice, než je záhy smažeš
		// od indexu se musí odečítat 1 protože index ukazuje vždy o znak napřed
		pcol = col - 1;
		pline = line;

		word.setLength(0);

		// konec řádku - \n
		if (ch == -2) {
			ch = nextChar(); // musím se ale posunout !!!
			return EOL;
		}

		// pokud jsem na konci souboru, tak to sdělím okamžitě
		if (ch == -1) {
			pcol++; // musí se posunout o jeden aby vycházelo, že ukazuje tam,
					// kde už není token
			return EOF;
		}

		// Tag - musí začínat '[', končit ']' a obsahovat pouze písmena, čísla
		// nebo '_'
		if (ch == '[') {

			// zkontroluj vnitřek tagu
			do {
				word.append((char) ch);
				ch = nextChar();

				// beru podtržítka,
				// čísla a písmena,
				// lomítko, ale pouze pokud je na pozici 1 ~> [/TAG]
				// a tag nesmí být roztaženej mezi řádky
			} while (Character.isLetterOrDigit(ch) || ch == '_' || (ch == '/' && word.length() == 1));

			// čtení bylo ukončeno kvůli konci řádku nebo EOF - ukonči řádku
			// (byl to text, nekončilo se ']')
			if (ch == -1 || ch == -2) {
				return TEXT;
			}

			// zkontroluj ']'
			if (ch == ']') {

				word.append((char) ch);
				ch = nextChar();

				// pokud je tag vlastně prázdný, tak to není tag, ale text
				if ((!word.toString().equals("[]")) && (!word.toString().equals("[/]"))) {

					// pokud tag začínal [/ tak to byl koncový tag
					return word.charAt(1) == '/' ? END_TAG : START_TAG;

				}

			}

			// pokud mne vyhodila značna tagu, tak ukončím tohle jako text
			// ale nebudu jí načítat - mohl bych přejet tag
			if (ch == '[') {
				return TEXT;
			}

			// jinak to nechám dojet jako text
		}

		/**
		 * Jinak je to text - dokud nedojde (začátek tagu, konec souboru, nová
		 * řádka)
		 */
		while ((ch != '[') && (ch != -1) && (ch != -2)) {
			word.append((char) ch);
			ch = nextChar();
		}

		// dočetl jsem text
		return TEXT;
	}
}
