package org.myftp.gattserver.grass3.articles.util;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import org.myftp.gattserver.grass3.articles.domain.Article;
import org.myftp.gattserver.grass3.articles.domain.ArticleJSResource;
import org.myftp.gattserver.grass3.articles.dto.ArticleDTO;
import org.myftp.gattserver.grass3.util.Mapper;
import org.springframework.stereotype.Component;

@Component("articlesMapper")
public class ArticlesMapper {

	/**
	 * Core mapper
	 */
	@Resource(name = "mapper")
	private Mapper mapper;

	/**
	 * Převede {@link Article} na {@link ArticleDTO}
	 */
	public ArticleDTO mapArticleForDetail(Article article) {
		ArticleDTO articleDTO = new ArticleDTO();

		articleDTO.setId(article.getId());
		articleDTO.setOutputHTML(article.getOutputHTML());
		articleDTO.setSearchableOutput(article.getSearchableOutput());

		Set<String> pluginCSSResources = new LinkedHashSet<String>();
		for (String resource : article.getPluginCSSResources()) {
			pluginCSSResources.add(resource);
		}
		articleDTO.setPluginCSSResources(pluginCSSResources);
		Set<String> pluginJSResources = new LinkedHashSet<String>();
		for (ArticleJSResource resource : article.getPluginJSResources()) {
			pluginJSResources.add(resource.getName());
		}
		articleDTO.setPluginJSResources(pluginJSResources);
		articleDTO.setText(article.getText());

		articleDTO.setContentNode(mapper.mapContentNodeForDetail(article
				.getContentNode()));
		return articleDTO;
	}

	/**
	 * Mapuje článek pro vyhledávání
	 */
	public ArticleDTO mapArticleForSearch(Article article) {
		ArticleDTO articleDTO = new ArticleDTO();
		articleDTO.setContentNode(mapper.mapContentNodeForDetail(article
				.getContentNode()));
		articleDTO.setId(article.getId());
		articleDTO.setSearchableOutput(article.getSearchableOutput());
		return articleDTO;
	}

	/**
	 * Převede {@link Article} na {@link ArticleDTO} určený pro přehled
	 * 
	 * @param article
	 * @return
	 */
	public ArticleDTO mapArticleForOverview(Article article) {
		ArticleDTO articleDTO = new ArticleDTO();
		articleDTO.setContentNode(mapper.mapContentNodeForOverview(article
				.getContentNode()));
		articleDTO.setId(article.getId());
		return articleDTO;
	}

	/**
	 * Převede kolekci {@link Article} na kolekci {@link ArticleDTO}
	 * 
	 * @param articles
	 *            vstupní kolekce entit {@link Article}
	 * @return
	 */
	public List<ArticleDTO> mapArticlesForOverview(List<Article> articles) {
		List<ArticleDTO> articleDTOs = new ArrayList<ArticleDTO>();
		for (Article article : articles) {
			articleDTOs.add(mapArticleForOverview(article));
		}
		return articleDTOs;
	}

	/**
	 * Převede kolekci {@link Article} na kolekci {@link ArticleDTO} pro
	 * přegenerování článku
	 * 
	 * @param articles
	 *            vstupní kolekce entit {@link Article}
	 * @return
	 */
	public List<ArticleDTO> mapArticlesForReprocess(List<Article> articles) {
		List<ArticleDTO> articleDTOs = new ArrayList<ArticleDTO>();
		for (Article article : articles) {
			articleDTOs.add(mapArticleForDetail(article));
		}
		return articleDTOs;
	}

	/**
	 * Převede kolekci {@link Article} na kolekci {@link ArticleDTO} určenou pro
	 * vyhledávání
	 * 
	 * @param articles
	 *            vstupní kolekce entit {@link Article}
	 * @return
	 */
	public List<ArticleDTO> mapArticlesForSearch(List<Article> articles) {
		List<ArticleDTO> articleDTOs = new ArrayList<ArticleDTO>();
		for (Article article : articles) {
			articleDTOs.add(mapArticleForSearch(article));
		}
		return articleDTOs;
	}

}
