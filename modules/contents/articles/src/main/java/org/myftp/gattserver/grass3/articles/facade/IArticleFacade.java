package org.myftp.gattserver.grass3.articles.facade;

import java.util.List;

import org.myftp.gattserver.grass3.articles.dto.ArticleDTO;
import org.myftp.gattserver.grass3.model.dto.NodeDTO;
import org.myftp.gattserver.grass3.model.dto.UserInfoDTO;

public interface IArticleFacade {

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
	 * @param category
	 *            kategorie do kteér se vkládá
	 * @param author
	 *            uživatel, který článek vytvořil
	 * @return {@code true} pokud vše dopadlo v pořádku, jinak {@code false}
	 */
	public boolean saveTemp(String name, String text, String tags,
			NodeDTO category, UserInfoDTO author);

	/**
	 * Smaže článek
	 * 
	 * @param article
	 *            článek ke smazání
	 * @return {@code true} pokud se zdařilo smazat jiank {@code false}
	 */
	public boolean deleteArticle(ArticleDTO articleDTO);

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
	 * @param articleDTO
	 *            původní článek
	 * @return {@code true} pokud se úprava zdařila, jinak {@code false}
	 */
	public boolean modifyArticle(String name, String text, String tags,
			boolean publicated, ArticleDTO articleDTO, String contextRoot);

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
	 * @param category
	 *            kategorie do které se vkládá
	 * @param author
	 *            uživatel, který článek vytvořil
	 * @return identifikátor článku pokud vše dopadlo v pořádku, jinak
	 *         {@code null}
	 */
	public Long saveArticle(String name, String text, String tags,
			boolean publicated, NodeDTO category, UserInfoDTO author,
			String contextRoot);

	/**
	 * Získá článek dle jeho identifikátoru
	 * 
	 * @param id
	 *            identifikátor
	 * @return DTO článku
	 */
	public ArticleDTO getArticleForDetail(Long id);

	/**
	 * Získá všechny články pro přegenerování
	 */
	public List<ArticleDTO> getAllArticlesForReprocess();

	/**
	 * Získá všechny články pro přehled
	 * 
	 * @return
	 */
	public List<ArticleDTO> getAllArticlesForOverview();

	/**
	 * Získá všechny články a namapuje je pro použití při vyhledávání
	 * 
	 * @return
	 */
	public List<ArticleDTO> getAllArticlesForSearch();

}
