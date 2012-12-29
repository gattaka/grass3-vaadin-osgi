package org.myftp.gattserver.grass3.search.service;

import java.util.List;
import java.util.Set;

import org.myftp.gattserver.grass3.model.dto.UserInfoDTO;

/**
 * {@link ISearchConnector} je způsob jakým sekce a obsahy obecně můžou
 * přihlásit svůj obsah k indexování a tím i k vyhledávání. Zároveň se tímto
 * přesuvem iniciativi poskytování vyhledáváného obsahu deleguje jeho filtrování
 * a authorizace na tyto konkrétní moduly.
 * 
 * @author gatt
 * 
 */
public interface ISearchConnector {

	/**
	 * Získá všechny dostupné entity k vyhledávání, které jsou viditelné pro
	 * daného uživatele
	 * 
	 * @param user
	 *            uživatel vůči kterému se vyhodnocuje viditelnost entit, ve
	 *            kterých se bude vyhledávat
	 * @return list entit k vyhledávání
	 */
	public List<SearchEntity> getAvailableSearchEntities(UserInfoDTO user);

	/**
	 * Získá pole, podle kterých lze blíže vyhledávat v obsazích
	 */
	public Set<Enum<?>> getSearchFields();

}
