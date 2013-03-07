package org.myftp.gattserver.grass3.articles.util;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import org.myftp.gattserver.grass3.articles.domain.Article;
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
	 * 
	 * @param article
	 * @return
	 */
	public ArticleDTO map(Article article) {

		ArticleDTO articleDTO = new ArticleDTO();

		articleDTO.setContentNode(mapper.map(article.getContentNode()));
		articleDTO.setId(article.getId());
		articleDTO.setOutputHTML(article.getOutputHTML());
		articleDTO.setSearchableOutput(article.getSearchableOutput());

		Set<String> pluginCSSResources = new LinkedHashSet<String>();
		for (String resource : article.getPluginCSSResources()) {
			pluginCSSResources.add(resource);
		}
		articleDTO.setPluginCSSResources(pluginCSSResources);
		Set<String> pluginJSResources = new LinkedHashSet<String>();
		for (String resource : article.getPluginJSResources()) {
			pluginJSResources.add(resource);
		}
		articleDTO.setPluginJSResources(pluginJSResources);
		articleDTO.setText(article.getText());

		return articleDTO;

	}

	/**
	 * Převede kolekci {@link Article} na kolekci {@link ArticleDTO}
	 * 
	 * @param articles
	 *            vstupní kolekce entit {@link Article}
	 * @return
	 */
	public List<ArticleDTO> map(List<Article> articles) {

		List<ArticleDTO> articleDTOs = new ArrayList<ArticleDTO>();

		for (Article article : articles) {
			articleDTOs.add(map(article));
		}

		return articleDTOs;

	}

}
