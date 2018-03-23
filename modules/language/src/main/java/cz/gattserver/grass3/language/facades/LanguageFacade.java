package cz.gattserver.grass3.language.facades;

import java.util.List;

import cz.gattserver.grass3.language.model.domain.ItemType;
import cz.gattserver.grass3.language.model.dto.LanguageItemTO;
import cz.gattserver.grass3.language.model.dto.LanguageTO;

public interface LanguageFacade {

	/**
	 * Získá přehled všech jazyků
	 * 
	 * @return jazyky
	 */
	List<LanguageTO> getLanguages();

	/**
	 * Uloží jazyk
	 * 
	 * @param languageTO
	 *            to jazyka
	 * @return db id, které bylo jazyku přiděleno
	 */
	long saveLanguage(LanguageTO languageTO);

	/**
	 * Získá všechny záznamy (stránkované)
	 * 
	 * @param languageId
	 *            id jazyka ze kterého mají být záznamy
	 * @param type
	 *            typ záznamu
	 * @param page
	 *            stránka
	 * @param size
	 *            velikost stránky
	 * @return list záznamů
	 */
	List<LanguageItemTO> getLanguageItems(long languageId, ItemType type, int page, int size);

	/**
	 * Získá záznamy na zkoušení
	 * 
	 * @param languageId
	 *            jazyk v rámci kterého se bude zkoušet
	 * @param type
	 *            typ záznamů, které se budou zkoušet
	 * @return list záznamů k přezkoušení
	 */
	List<LanguageItemTO> getLanguageItemsForTest(long languageId, ItemType type);

	/**
	 * Získá počet všech záznamů
	 * 
	 * @param languageId
	 *            id jazyka ze kterého mají být záznamy
	 * @param type
	 *            typ záznamu
	 * @return počet záznamů
	 */
	int countLanguageItems(long languageId, ItemType type);

	/**
	 * Získá záznam dle id
	 */
	LanguageItemTO getLanguageItemById(Long id);

	/**
	 * Uloží záznam
	 */
	Long saveLanguageItem(LanguageItemTO itemTO);

	/**
	 * Aktualizuje stav záznamu po zkoušení
	 * 
	 * @param item
	 *            záznam
	 * @param výsledek
	 *            zkoušení
	 */
	void updateItemAfterTest(LanguageItemTO item, boolean success);

	/**
	 * Smaže daný záznam
	 * 
	 * @param item
	 */
	void deleteLanguageItem(LanguageItemTO item);

}
