package cz.gattserver.grass3.articles.util;

import java.util.List;

import cz.gattserver.grass3.articles.domain.Article;
import cz.gattserver.grass3.articles.dto.ArticleDTO;
import cz.gattserver.grass3.articles.dto.ArticleDraftOverviewDTO;

public interface ArticlesMapper {

	/**
	 * Převede {@link Article} na {@link ArticleDTO}
	 */
	public ArticleDTO mapArticleForDetail(Article article);

	/**
	 * Mapuje článek pro vyhledávání
	 */
	public ArticleDTO mapArticleForSearch(Article article);

	/**
	 * Převede kolekci {@link Article} na kolekci {@link ArticleDTO} pro
	 * přegenerování článku
	 * 
	 * @param articles
	 *            vstupní kolekce entit {@link Article}
	 * @return
	 */
	public List<ArticleDTO> mapArticlesForReprocess(List<Article> articles);

	/**
	 * Převede kolekci {@link Article} na kolekci {@link ArticleDTO} určenou pro
	 * vyhledávání
	 * 
	 * @param articles
	 *            vstupní kolekce entit {@link Article}
	 * @return
	 */
	public List<ArticleDTO> mapArticlesForSearch(List<Article> articles);

	/**
	 * Převede kolekci {@link Article} na kolekci
	 * {@link ArticleDraftOverviewDTO} určenou pro menu výběru rozpracovaného
	 * článku
	 * 
	 * @param articles
	 *            vstupní kolekce entit {@link Article}
	 * @return
	 */
	public List<ArticleDraftOverviewDTO> mapArticlesForDraftOverview(List<Article> articles);

	/**
	 * Převede {@link Article} na {@link ArticleDraftOverviewDTO} určený pro
	 * menu výběru rozpracovaného článku
	 * 
	 * @param article
	 *            vstupní {@link Article}
	 * @return
	 */
	public ArticleDraftOverviewDTO mapArticleForDraftOverview(Article article);

}
