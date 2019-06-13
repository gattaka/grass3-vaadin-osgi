package cz.gattserver.grass3.articles.services;

import java.util.List;

import cz.gattserver.grass3.articles.interfaces.ArticleTO;
import cz.gattserver.grass3.articles.interfaces.ArticleDraftOverviewTO;
import cz.gattserver.grass3.articles.interfaces.ArticleRESTTO;
import cz.gattserver.grass3.articles.model.domain.Article;

public interface ArticlesMapperService {

	/**
	 * Převede {@link Article} na {@link ArticleTO}
	 */
	public ArticleTO mapArticleForDetail(Article article);

	/**
	 * Převede {@link Article} na {@link ArticleRESTTO}
	 */
	public ArticleRESTTO mapArticleForREST(Article article);

	/**
	 * Mapuje článek pro vyhledávání
	 */
	public ArticleTO mapArticleForSearch(Article article);

	/**
	 * Převede kolekci {@link Article} na kolekci {@link ArticleTO} určenou pro
	 * vyhledávání
	 * 
	 * @param articles
	 *            vstupní kolekce entit {@link Article}
	 * @return
	 */
	public List<ArticleTO> mapArticlesForSearch(List<Article> articles);

	/**
	 * Převede kolekci {@link Article} na kolekci {@link ArticleDraftOverviewTO}
	 * určenou pro menu výběru rozpracovaného článku
	 * 
	 * @param articles
	 *            vstupní kolekce entit {@link Article}
	 * @return
	 */
	public List<ArticleDraftOverviewTO> mapArticlesForDraftOverview(List<Article> articles);

	/**
	 * Převede {@link Article} na {@link ArticleDraftOverviewTO} určený pro menu
	 * výběru rozpracovaného článku
	 * 
	 * @param article
	 *            vstupní {@link Article}
	 * @return
	 */
	public ArticleDraftOverviewTO mapArticleForDraftOverview(Article article);

}
