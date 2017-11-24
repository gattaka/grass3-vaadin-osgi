package cz.gattserver.grass3.articles.editor.parser.util;

/**
 * Ořezává výsledný text z parseru do tvaru, kde je jenom plain-text obsah.
 * Veškeré formátování a skripty jsou odstraněny. Hledání v takovém textu je pak
 * efektivnější a přesnější (nejsou nacházeny jména tagů apod.)
 * 
 * @author gatt
 * 
 */
public class HTMLTrimmer {

	private HTMLTrimmer() {
	}

	public static String trim(String text) {

		// vynechávám text ?
		boolean trimMode = false;

		// jsem na prvním znaku za vynechaným textem (mám vložit mezeru ?)
		boolean justEnded = false;

		StringBuilder stringBuilder = new StringBuilder();

		for (int index = 0; index < text.length(); index++) {

			char c = text.charAt(index);

			// započal tag - zapni trim
			if (c == '<') {
				trimMode = true;
				continue;
			}

			// skončil tag - vypni trim
			if (c == '>') {
				trimMode = false;
				justEnded = true;
				continue;
			}

			// pokud jedu trim, ignoruj znaky, jinak je připisuj
			if (trimMode) {
				continue;
			} else {
				if (justEnded) {
					stringBuilder.append(' ');
					justEnded = false;
				}
				stringBuilder.append(c);
			}

		}

		return stringBuilder.toString();

	}

}
