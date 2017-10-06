package cz.gattserver.grass3.articles.facade;

import java.util.Collection;
import java.util.List;

import cz.gattserver.grass3.articles.dto.ArticleDTO;

public interface ArticleFacade {

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
	public Long saveArticle(String name, String text, Collection<String> tags, boolean publicated, Long nodeId,
			Long authorId, String contextRoot, ArticleProcessForm processForm, Long existingId);

	/**
	 * Smaže článek
	 * 
	 * @param article
	 *            článek ke smazání
	 * @return {@code true} pokud se zdařilo smazat jiank {@code false}
	 */
	public void deleteArticle(Long id);

	/**
	 * Získá článek dle jeho identifikátoru
	 * 
	 * @param id
	 *            identifikátor
	 * @return DTO článku
	 */
	public ArticleDTO getArticleForDetail(Long id);

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
	public List<ArticleDTO> getAllArticlesForSearch();

}
