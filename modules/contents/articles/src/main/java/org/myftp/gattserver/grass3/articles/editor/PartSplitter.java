package org.myftp.gattserver.grass3.articles.editor;

import org.myftp.gattserver.grass3.articles.parser.Position;

/**
 * Úpravy článků po částech by měly fungovat tak, že během parsování článku se
 * "oanotují" části textu značkou s intervalem pozic, na kterých je daná část ve
 * zdrojovém textu.
 * 
 * Oanotovány jsou zvlášt:
 * <ul>
 * <li>text - do konce řádku, bez znaku EOL</li>
 * <li>text - od začátku řádku, včetně EOL minulého řádku (to umožňuje provádět
 * mazání řádků bez toho aby tam zbyly prázdné mezery</li>
 * <li>elementy, z vnějšku, včetně počátečního a koncového tagu, aby šlo měnit,
 * co je to za element</li>
 * </ul>
 * 
 * Pozice jsou číslovány od 0,0 (řádek, sloupec), přičemž <b>počáteční
 * pozice</b> označuje první znak <b>patřící do části</b> a <b>koncová
 * pozice</b> první znak, co tam už <b>nepatří</b
 * 
 * @author gatt
 * 
 */
public class PartSplitter {

	/**
	 * Oddělovač řádků - jeden jak je zapsaný a druhý jak opravdu vypadá
	 */
	public static final char DELIMITER = '\n';

	private StringBuilder prefixRestBuilder = new StringBuilder();
	private StringBuilder suffixRestBuilder = new StringBuilder();
	private StringBuilder partBuilder = new StringBuilder();

	public StringBuilder getPrefixRestBuilder() {
		return prefixRestBuilder;
	}

	public StringBuilder getSuffixRestBuilder() {
		return suffixRestBuilder;
	}

	public StringBuilder getPartBuilder() {
		return partBuilder;
	}

	/**
	 * Získá podřetězec z bufferu, který může být na konci řádky a tím pádem je
	 * potřeba ošetřit, zda přidat EOL znak.
	 * 
	 * @param buffer
	 *            buffer s rozpracovaným textem
	 * @param endCol
	 *            pozice, která vymezuje první znak, který do bufferu už nemá
	 *            patřit
	 * @return
	 */
	// private String getSubstring(String buffer, int startCol, int endCol) {
	// /**
	// * Ze zlomové řádky nahraj zbytek - je třeba zkontrolovat zda poslední
	// * znak není konec řádku, protože ten není v bufferu a v tom případě se
	// * musí vzít o znak "méně" a přidat EOL
	// */
	// if (buffer.length() <= endCol) {
	// return buffer.substring(startCol) + DELIMITER_CHAR;
	// } else {
	// return buffer.substring(startCol, endCol);
	// }
	// }

	private enum Mode {
		PREFIX, PART, SUFFIX
	}

	/**
	 * Rozdělí vstupní text na prefix a suffix ponechávané části textu a na část
	 * mezi nimi, která se bude samostatně upravovat
	 * 
	 * @param source
	 *            zdrojový text
	 * @param from
	 *            pozice řádek-sloupec, která označuje první znak <b>patřící do
	 *            upravované části</b>
	 * @param to
	 *            pozice řádek-sloupce, která označuje poslední znak <b>patřící
	 *            do upravované části</b>
	 */
	public PartSplitter(String source, Position from, Position to) {

		int line = 0;
		int ch = 0;
		Mode mode = Mode.PREFIX;
		for (char c : source.toCharArray()) {
			if (from.getLine() == line && from.getCol() == ch)
				mode = Mode.PART;
			if (to.getLine() == line && to.getCol() == ch)
				mode = Mode.SUFFIX;

			switch (mode) {
			case PREFIX:
				prefixRestBuilder.append(c);
				break;
			case PART:
				partBuilder.append(c);
				break;
			case SUFFIX:
				suffixRestBuilder.append(c);
			}

			if (c == DELIMITER) {
				line++;
				ch = 0;
			} else {
				ch++;
			}
		}

	}

	// private void old(String source, Position from, Position to) {
	// Scanner scanner = new Scanner(source); // inicializuj
	// // scanner
	// scanner.useDelimiter(DELIMITER_CHAR); // chci aby reagoval i na mezery
	//
	// /**
	// * Nejprve je potřeba odkrokovat část, kterou ještě nebudu upravovat -
	// * musím tak dojít k místu od kterého se bude editovat po cestě si musím
	// * vše zapisovat, protože to ponechávám a bude to dávat pak před
	// * upravenou část - dokud nejsem na poslední řádce ponechávaného textu,
	// * můžu to rovnou ukládat do toho prefixRestBuilder - poslední řádka
	// * bude již z části editovaná, takže tu nemůžu jen tak vložit do zbytku
	// */
	// String buffer = "";
	// int line = 0;
	// prefixRestBuilder = new StringBuilder();
	// for (; line < from.getLine(); line++) {
	// buffer = scanner.nextLine();
	//
	// /**
	// * pokud jsem na řádce nižší, než je řádka zlomu, ber celý buffer
	// * (+konec řádky !),
	// */
	// prefixRestBuilder.append(buffer).append(DELIMITER_CHAR);
	// }
	// /**
	// * + zbytek
	// */
	// buffer = scanner.nextLine();
	// prefixRestBuilder.append(getSubstring(buffer, 0, from.getCol()));
	//
	// /**
	// * Nyní pokračuje odečítání editované části - pokud na stejné řádce na
	// * které jsem i končím, pak udělej jenom substring z řádky - jinak dočti
	// * řádku a načítej dokud nenarazíš na konec výběru
	// */
	// partBuilder = new StringBuilder();
	// if (from.getLine() != to.getLine()) {
	// partBuilder.append(buffer.substring(from.getCol()));
	// for (; line < to.getLine(); line++) {
	//
	// /**
	// * Dokud patříš do výběru, přidávej celé řádky (+konec řádky !)
	// */
	// partBuilder.append(buffer = scanner.nextLine()).append(DELIMITER_CHAR);
	// }
	// /**
	// * + zbytek
	// */
	// partBuilder.append(getSubstring(buffer, 0, to.getCol()));
	// } else {
	// partBuilder.append(getSubstring(buffer, from.getCol(), to.getCol()));
	// }
	//
	// /**
	// * A zbytek původní části - pokud ukazuje zbytek za hranici bufferu, pak
	// * je to všechno z této řádky a pokračuj na další, pokud existuje další
	// * řádka, dopiš EOL, jinak nedopisuj ani ten
	// */
	// suffixRestBuilder = new StringBuilder();
	// if (buffer.length() > to.getCol())
	// suffixRestBuilder.append(buffer.substring(to.getCol()));
	// while (scanner.hasNextLine()) {
	// suffixRestBuilder.append(DELIMITER_CHAR);
	// suffixRestBuilder.append(scanner.nextLine());
	// }
	//
	// }

}
