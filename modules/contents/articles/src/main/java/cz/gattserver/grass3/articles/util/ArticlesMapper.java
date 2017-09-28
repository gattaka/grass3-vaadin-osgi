package cz.gattserver.grass3.articles.util;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cz.gattserver.grass3.articles.domain.Article;
import cz.gattserver.grass3.articles.domain.ArticleJSResource;
import cz.gattserver.grass3.articles.dto.ArticleDTO;
import cz.gattserver.grass3.model.util.CoreMapper;

@Component
public class ArticlesMapper {

	/**
	 * Core mapper
	 */
	@Autowired
	private CoreMapper mapper;

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

		articleDTO.setContentNode(mapper.mapContentNodeForDetail(article.getContentNode()));
		return articleDTO;
	}

	/**
	 * Mapuje článek pro vyhledávání
	 */
	public ArticleDTO mapArticleForSearch(Article article) {
		ArticleDTO articleDTO = new ArticleDTO();
		articleDTO.setContentNode(mapper.mapContentNodeForDetail(article.getContentNode()));
		articleDTO.setId(article.getId());
		articleDTO.setSearchableOutput(article.getSearchableOutput());
		return articleDTO;
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
