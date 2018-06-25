package cz.gattserver.grass3.language.util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import cz.gattserver.grass3.language.model.domain.LanguageItem;
import cz.gattserver.grass3.language.model.dto.CrosswordCell;
import cz.gattserver.grass3.language.model.dto.CrosswordTO;

public class CrosswordBuilder {

	private static final int HINT_CELL_OFFSET = 1;

	private int sideSize;
	private List<LanguageItem> dictionary;

	private CrosswordTO crosswordTO;
	private Set<String> usedWords;

	public CrosswordBuilder(int sideSize, List<LanguageItem> dictionary) {
		this.dictionary = dictionary;
		this.sideSize = sideSize;
	}

	public CrosswordTO build() {
		crosswordTO = new CrosswordTO(sideSize, sideSize);
		usedWords = new HashSet<>();

		// 1. počáteční slovo
		LanguageItem item = dictionary.get(new Random().nextInt(dictionary.size()));
		// LanguageItem item = dictionary.get(0);
		crosswordTO.insertWord(0, HINT_CELL_OFFSET, item.getContent(), item.getTranslation(), true);
		usedWords.add(item.getContent());

		// opakovaně v podčtvercích
		for (int offset = 0; offset < sideSize; offset += 2) {

			// 2. vertikální slova na první slovo
			for (int x = HINT_CELL_OFFSET; x < sideSize; x += 2)
				fillCrosswordItem(x + offset, 0 + offset, false);

			// 3. horizontální slova na existující slova
			for (int y = HINT_CELL_OFFSET; y < sideSize; y += 2)
				fillCrosswordItem(0 + offset, y + offset, true);
		}

		return crosswordTO;
	}

	private void fillCrosswordItem(int x, int y, boolean horizontally) {
		int maxLength = horizontally ? crosswordTO.getWidth() - x - 1 : crosswordTO.getHeight() - y - 1;
		List<LanguageItem> workList = new ArrayList<>(dictionary);

		for (int i = 0; i < workList.size(); i++) {
			LanguageItem item = randomRemove(workList);
			if (item.getContent().length() <= maxLength && !usedWords.contains(item.getContent())
					&& fits(item.getContent(), x, y, horizontally)) {
				crosswordTO.insertWord(x, y, item.getContent(), item.getTranslation(), horizontally);
				usedWords.add(item.getContent());
				break;
			}
		}
	}

	/**
	 * Kontroluje, zda je možné na dané souřadnice daným směrem zapsat dané slovo
	 * 
	 * @param word
	 *            zapisované slovo
	 * @param x
	 *            počáteční souřadnice x, od které se bude zapisovat (včetně hint buňky)
	 * @param y
	 *            počáteční souřadnice y, od které se bude zapisovat (včetně hint buňky)
	 * @param horizontally
	 *            přepínač, zda se bude zapisovat vodorovně (<code>true</code>) nebo svisle
	 * @return <code>true</code>, pokud je zápis slova možný
	 */
	private boolean fits(String word, int x, int y, boolean horizontally) {

		// aspoň jedno písmeno se musí protínat s jiným slovem
		boolean emptyCrossSection = true;

		// vejde se tam slovo vůbec?
		if (horizontally && x + word.length() > crosswordTO.getWidth()
				|| !horizontally && y + word.length() > crosswordTO.getHeight())
			return false;

		// počáteční souřadnice musí být úplně prázdné, jinak tam nepůjde dát hint
		CrosswordCell cell = crosswordTO.getCell(x, y);
		if (cell != null)
			return false;

		// prochází postupně buňky umístění slova a kontroluje, zda v nich (nebo v okolí) nedojde ke konfliktu
		for (int i = 0; i < word.length() + 1; i++) {
			int checkX = horizontally ? x + HINT_CELL_OFFSET + i : x;
			int checkY = horizontally ? y : y + HINT_CELL_OFFSET + i;
			cell = crosswordTO.getCell(checkX, checkY);

			if (cell == null) {
				if (horizontally) {
					// Pokud slovo pokládám horizontálně, pak buď:
					// 1.) jsem na průsečíku slov a pak se musí zkontrolovat jestli jsme na stejném písmenu -> 1.a/2.a
					// 2.) jsem na prázdné buňce a nad/pod touto buňkou musí být také prázdná (jinak jsem přidal písmeno
					// k
					// existujícímu slovu, které končí/prochází na touto buňkou)
					CrosswordCell aboveCell = crosswordTO.getCell(checkX, checkY - 1);
					CrosswordCell belowCell = crosswordTO.getCell(checkX, checkY + 1);
					if (aboveCell != null && aboveCell.isWriteAllowed()
							|| belowCell != null && belowCell.isWriteAllowed())
						return false;
				} else {
					// Pokud slovo pokládám vertikálně, pak buď:
					// 1.) jsem na průsečíku slov a pak se musí zkontrolovat jestli jsme na stejném písmenu -> 1.a/2.a
					// 2.) jsem na prázdné buňce a před/za touto buňkou musí být také prázdná (jinak jsem přidal písmeno
					// k
					// existujícímu slovu, které končí/prochází na touto buňkou)
					CrosswordCell prevCell = crosswordTO.getCell(checkX - 1, checkY);
					CrosswordCell nextCell = crosswordTO.getCell(checkX + 1, checkY);
					if (prevCell != null && prevCell.isWriteAllowed() || nextCell != null && nextCell.isWriteAllowed())
						return false;
				}
			}

			if (i < word.length()) {
				// 1.a/2.a jsem na průsečíku slov a pak se musí zkontrolovat jestli jsme na stejném písmenu
				if (cell != null)
					emptyCrossSection = false;
				if (cell != null && !String.valueOf(word.charAt(i)).equals(cell.getValue()))
					return false;
			} else {
				// souřadnice za koncem musí být prázdné nebo tam být mezera, aby se konec slova nepropojil s vedlejším
				// obsahem, se kterým sousedí
				if (cell != null && !cell.getValue().equals(" "))
					return false;
			}
		}

		return !emptyCrossSection;
	}

	private LanguageItem randomRemove(List<LanguageItem> list) {
		int index = new Random().nextInt(list.size());
		// int index = 0;
		return list.remove(index);
	}

}
