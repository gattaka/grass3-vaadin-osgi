package cz.gattserver.grass3.articles.services;

import java.util.Collection;
import java.util.List;

import cz.gattserver.grass3.articles.interfaces.ArticleTO;
import cz.gattserver.grass3.articles.interfaces.ArticleDraftOverviewTO;

public interface ArticleService {

	/**
	 * Uloží článek
	 * 
	 * @param name
	 *            název článku
	 * @param text
	 *            obsah článku
	 * @param tags
	 *            klíčová slova článku
	 * @param publicated
	 *            je článek publikován ?
	 * @param nodeId
	 *            kategorie do které se vkládá
	 * @param authorId
	 *            uživatel, který článek vytvořil
	 * @param contextRoot
	 *            od jakého adresového kořene se mají generovat linky v článku
	 * @param processForm
	 *            jakým způsobem se má článek zpracovat
	 * @param existingId
	 *            id, jde-li o úpravu existujícího článku
	 * @param partNumber
	 *            číslo části, je-li editována specifická část článku (povinné,
	 *            pouze jde-li o ukládání draftu)
	 * @param draftSourceId
	 *            id existujícího zdrojového článku, ke kterému je ukládán draft
	 *            (jde-li o draft existujícího článku)
	 * @return identifikátor článku
	 * 
	 */
	public long saveArticle(String name, String text, Collection<String> tags, boolean publicated, long nodeId,
			long authorId, String contextRoot, ArticleProcessMode processForm, Long existingId, Integer partNumber,
			Long draftSourceId);

	/**
	 * Uloží článek
	 * 
	 * @param name
	 *            název článku
	 * @param text
	 *            obsah článku
	 * @param tags
	 *            klíčová slova článku
	 * @param publicated
	 *            je článek publikován ?
	 * @param nodeId
	 *            kategorie do které se vkládá
	 * @param authorId
	 *            uživatel, který článek vytvořil
	 * @param contextRoot
	 *            od jakého adresového kořene se mají generovat linky v článku
	 * @param processForm
	 *            jakým způsobem se má článek zpracovat
	 * @param existingId
	 *            id, jde-li o úpravu existujícího článku
	 * @return identifikátor článku pokud vše dopadlo v pořádku, jinak
	 *         {@code null}
	 */
	public long saveArticle(String name, String text, Collection<String> tags, boolean publicated, long nodeId,
			long authorId, String contextRoot, ArticleProcessMode processForm, Long existingId);

	/**
	 * Smaže článek
	 * 
	 * @param article
	 *            článek ke smazání
	 * @return {@code true} pokud se zdařilo smazat jiank {@code false}
	 */
	public void deleteArticle(long id);

	/**
	 * Získá článek dle jeho identifikátoru
	 * 
	 * @param id
	 *            identifikátor
	 * @return DTO článku
	 */
	public ArticleTO getArticleForDetail(long id);

	/**
	 * Spustí přegenerování
	 * 
	 * @param contextRoot
	 */
	public void reprocessAllArticles(String contextRoot);

	/**
	 * Získá všechny články a namapuje je pro použití při vyhledávání
	 * 
	 * @return
	 */
	public List<ArticleTO> getAllArticlesForSearch();

	/**
	 * Získá všechny rozpracované články viditelné daným uživatelem
	 * 
	 * @param userId
	 *            id uživatele, kterým je omezena viditelnost na rozpracované
	 *            články
	 * 
	 * @return
	 */
	public List<ArticleDraftOverviewTO> getDraftsForUser(long userId);

}
