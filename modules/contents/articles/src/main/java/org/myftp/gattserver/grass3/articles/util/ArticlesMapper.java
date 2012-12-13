package org.myftp.gattserver.grass3.articles.util;

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
		articleDTO.setPluginCSSResources(article.getPluginCSSResources());
		articleDTO.setPluginJSResources(article.getPluginJSResources());
		articleDTO.setText(article.getText());

		return articleDTO;

	}

}
