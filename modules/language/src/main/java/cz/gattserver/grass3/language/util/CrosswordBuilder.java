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

	private boolean fits(String word, int x, int y, boolean horizontally) {
		int start = horizontally ? x + 1 : y + 1;
		int limit = horizontally ? crosswordTO.getWidth() : crosswordTO.getHeight();
		int charCounter = 0;

		// aspoň jedno slovo se protíná s tímto slovem
		boolean emptyCrossSection = true;

		// x:y musí být prázdné, jinak tam nepůjde dát hint číslo
		CrosswordCell cell = crosswordTO.getCell(x, y);
		if (cell != null)
			return false;

		for (int i = start; i < limit && charCounter < word.length() + 1; i++) {
			int checkX = horizontally ? i : x;
			int checkY = horizontally ? y : i;
			cell = crosswordTO.getCell(checkX, checkY);

			// vlevo nebo nad musí být volno, abych se nepřilepil k existujícímu
			// obsahum, výjimkou je, pokud je tam hint nebo jsem na průsečíku
			CrosswordCell nearCell = horizontally ? crosswordTO.getCell(checkX, checkY - 1)
					: crosswordTO.getCell(checkX - 1, checkY);
			if (cell == null && nearCell != null && nearCell.isWriteAllowed())
				return false;

			if (charCounter < word.length()) {
				if (cell != null)
					emptyCrossSection = false;
				if (cell != null && !String.valueOf(word.charAt(charCounter)).equals(cell.getValue()))
					return false;
			} else {
				// aby se nepropojil konec s vedlejším obsahem, musí být za
				// slovem mezera nebo nevyplněná buňka
				if (cell != null && !cell.getValue().equals(" "))
					return false;
			}
			charCounter++;
		}

		return !emptyCrossSection;
	}

	private LanguageItem randomRemove(List<LanguageItem> list) {
		int index = new Random().nextInt(list.size());
		// int index = 0;
		return list.remove(index);
	}

}
