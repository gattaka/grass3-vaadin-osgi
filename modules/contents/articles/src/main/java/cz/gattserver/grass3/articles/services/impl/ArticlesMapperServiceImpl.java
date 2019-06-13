package cz.gattserver.grass3.articles.services.impl;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cz.gattserver.grass3.articles.interfaces.ArticleTO;
import cz.gattserver.grass3.articles.interfaces.ArticleDraftOverviewTO;
import cz.gattserver.grass3.articles.interfaces.ArticleRESTTO;
import cz.gattserver.grass3.articles.model.domain.Article;
import cz.gattserver.grass3.articles.model.domain.ArticleJSCode;
import cz.gattserver.grass3.articles.model.domain.ArticleJSResource;
import cz.gattserver.grass3.articles.services.ArticlesMapperService;
import cz.gattserver.grass3.services.CoreMapperService;

@Service
public class ArticlesMapperServiceImpl implements ArticlesMapperService {

	/**
	 * Core mapper
	 */
	@Autowired
	private CoreMapperService mapper;

	public <T extends ArticleRESTTO> T mapArticleBase(Article article, T articleDTO) {
		articleDTO.setId(article.getId());
		articleDTO.setOutputHTML(article.getOutputHTML());

		Set<String> pluginCSSResources = new LinkedHashSet<>();
		for (String resource : article.getPluginCSSResources())
			pluginCSSResources.add(resource);
		articleDTO.setPluginCSSResources(pluginCSSResources);

		Set<String> pluginJSResources = new LinkedHashSet<>();
		for (ArticleJSResource resource : article.getPluginJSResources())
			pluginJSResources.add(resource.getName());
		articleDTO.setPluginJSResources(pluginJSResources);

		articleDTO.setContentNode(mapper.mapContentNodeForDetail(article.getContentNode()));
		return articleDTO;
	}

	public ArticleTO mapArticleForDetail(Article article) {
		ArticleTO articleDTO = mapArticleBase(article, new ArticleTO());

		Set<String> pluginJSCodes = new LinkedHashSet<>();
		for (ArticleJSCode code : article.getPluginJSCodes())
			pluginJSCodes.add(code.getContent());
		articleDTO.setPluginJSCodes(pluginJSCodes);

		articleDTO.setText(article.getText());

		articleDTO.setContentNode(mapper.mapContentNodeForDetail(article.getContentNode()));
		return articleDTO;
	}

	public ArticleRESTTO mapArticleForREST(Article article) {
		ArticleRESTTO articleDTO = mapArticleBase(article, new ArticleRESTTO());
		articleDTO.setContentNode(mapper.mapContentNodeForDetail(article.getContentNode()));
		return articleDTO;
	}

	/**
	 * Mapuje článek pro vyhledávání
	 */
	public ArticleTO mapArticleForSearch(Article article) {
		ArticleTO articleDTO = new ArticleTO();
		articleDTO.setContentNode(mapper.mapContentNodeForDetail(article.getContentNode()));
		articleDTO.setId(article.getId());
		articleDTO.setSearchableOutput(article.getSearchableOutput());
		return articleDTO;
	}

	/**
	 * Převede kolekci {@link Article} na kolekci {@link ArticleTO} určenou pro
	 * vyhledávání
	 * 
	 * @param articles
	 *            vstupní kolekce entit {@link Article}
	 * @return
	 */
	public List<ArticleTO> mapArticlesForSearch(List<Article> articles) {
		List<ArticleTO> articleDTOs = new ArrayList<>();
		for (Article article : articles)
			articleDTOs.add(mapArticleForSearch(article));
		return articleDTOs;
	}

	/**
	 * Převede kolekci {@link Article} na kolekci {@link ArticleDraftOverviewTO}
	 * určenou pro menu výběru rozpracovaného článku
	 * 
	 * @param articles
	 *            vstupní kolekce entit {@link Article}
	 * @return
	 */
	public List<ArticleDraftOverviewTO> mapArticlesForDraftOverview(List<Article> articles) {
		List<ArticleDraftOverviewTO> articleDTOs = new ArrayList<>();
		for (Article article : articles)
			articleDTOs.add(mapArticleForDraftOverview(article));
		return articleDTOs;
	}

	/**
	 * Převede {@link Article} na {@link ArticleDraftOverviewTO} určený pro menu
	 * výběru rozpracovaného článku
	 * 
	 * @param article
	 *            vstupní {@link Article}
	 * @return
	 */
	public ArticleDraftOverviewTO mapArticleForDraftOverview(Article article) {
		ArticleDraftOverviewTO articleDTO = new ArticleDraftOverviewTO();
		articleDTO.setContentNode(mapper.mapContentNodeForDetail(article.getContentNode()));
		articleDTO.setId(article.getId());
		articleDTO.setText(article.getText());
		articleDTO.setPartNumber(article.getPartNumber());
		return articleDTO;
	}

}
