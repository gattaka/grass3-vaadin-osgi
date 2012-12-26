package org.myftp.gattserver.grass3.articles.util;

import java.util.HashSet;
import java.util.Set;

import org.myftp.gattserver.grass3.articles.domain.Article;
import org.myftp.gattserver.grass3.articles.dto.ArticleDTO;
import org.myftp.gattserver.grass3.util.Mapper;

public enum ArticlesMapper {

	INSTANCE;

	/**
	 * Core mapper
	 */
	private Mapper mapper = Mapper.INSTANCE;

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

		Set<String> pluginCSSResources = new HashSet<String>();
		for (String resource : article.getPluginCSSResources()) {
			pluginCSSResources.add(resource);
		}
		articleDTO.setPluginCSSResources(pluginCSSResources);
		Set<String> pluginJSResources = new HashSet<String>();
		for (String resource : article.getPluginJSResources()) {
			pluginJSResources.add(resource);
		}
		articleDTO.setPluginJSResources(pluginJSResources);
		articleDTO.setText(article.getText());

		return articleDTO;

	}

}
