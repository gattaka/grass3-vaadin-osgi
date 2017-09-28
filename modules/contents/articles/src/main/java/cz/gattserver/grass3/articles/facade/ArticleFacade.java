package cz.gattserver.grass3.articles.facade;

import java.util.Collection;
import java.util.List;

import cz.gattserver.grass3.articles.dto.ArticleDTO;
import cz.gattserver.grass3.model.dto.NodeDTO;
import cz.gattserver.grass3.model.dto.UserInfoDTO;

public interface ArticleFacade {

	/**
	 * Zpracuje článek a vrátí jeho HMTL výstup.
	 * 
	 * @param text
	 *            vstupní text článku
	 * @return výstupní DTO článku, pokud se překlad zdařil, jinak {@code null}
	 */
	public ArticleDTO processPreview(String text, String contextRoot);

	/**
	 * Uloží rozpracovaný článek - nepřekládá ho, jenom uloží obsah polí v
	 * editoru
	 * 
	 * @param name
	 *            název článku
	 * @param text
	 *            obsah článku
	 * @param tags
	 *            klíčová slova článku
	 * @param node
	 *            kategorie do kteér se vkládá
	 * @param author
	 *            uživatel, který článek vytvořil
	 * @return {@code true} pokud vše dopadlo v pořádku, jinak {@code false}
	 */
	public void saveTemp(String name, String text, String tags, NodeDTO node, UserInfoDTO author);

	/**
	 * Smaže článek
	 * 
	 * @param article
	 *            článek ke smazání
	 * @return {@code true} pokud se zdařilo smazat jiank {@code false}
	 */
	public void deleteArticle(ArticleDTO articleDTO);

	/**
	 * Upraví článek
	 * 
	 * @param name
	 *            název článku
	 * @param text
	 *            obsah článku
	 * @param tags
	 *            klíčová slova článku
	 * @param publicated
	 *            je článek publikován ?
	 * @param articleId
	 *            id původního článku
	 * @param contentNodeId
	 *            id contentNode původního článku
	 * @return {@code true} pokud se úprava zdařila, jinak {@code false}
	 */
	public void modifyArticle(String name, String text, Collection<String> tags, boolean publicated, Long articleId,
			Long contentNodeId, String contextRoot);

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
	 * @param node
	 *            kategorie do které se vkládá
	 * @param author
	 *            uživatel, který článek vytvořil
	 * @return identifikátor článku pokud vše dopadlo v pořádku, jinak
	 *         {@code null}
	 */
	public Long saveArticle(String name, String text, Collection<String> tags, boolean publicated, NodeDTO node,
			UserInfoDTO author, String contextRoot);

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
