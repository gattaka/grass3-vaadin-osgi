package cz.gattserver.grass3.search.service;

import java.util.List;

import cz.gattserver.grass3.interfaces.UserInfoTO;

/**
 * {@link SearchConnector} je způsob jakým sekce a obsahy obecně můžou
 * přihlásit svůj obsah k indexování a tím i k vyhledávání. Zároveň se tímto
 * přesuvem iniciativi poskytování vyhledáváného obsahu deleguje jeho filtrování
 * a authorizace na tyto konkrétní moduly.
 * 
 * @author gatt
 * 
 */
public interface SearchConnector {

	/**
	 * Získá všechny dostupné entity k vyhledávání, které jsou viditelné pro
	 * daného uživatele
	 * 
	 * @param user
	 *            uživatel vůči kterému se vyhodnocuje viditelnost entit, ve
	 *            kterých se bude vyhledávat
	 * @return list entit k vyhledávání
	 */
	public List<SearchEntity> getAvailableSearchEntities(UserInfoTO user);

	/**
	 * Získá pole, podle kterých lze blíže vyhledávat v obsazích
	 */
	public Enum<? extends SearchField>[] getSearchFields();

	/**
	 * Získá unikátní název, dle kterého se bude odlišovat prostor hledání. Mělo
	 * by se jednat o první pád názvu modulu. Například "Modul článků"
	 */
	public String getModuleId();

	/**
	 * Získá jméno pole, které se neprohledává ale dává se do něj odkaz na
	 * nalezený obsah. Většinou postačí vracet rovnou například "link", jde o
	 * to, aby tento řetězec nekolidoval s žádným z názvů z {@link SearchField}
	 * enum
	 */
	public String getLinkFieldName();

}
