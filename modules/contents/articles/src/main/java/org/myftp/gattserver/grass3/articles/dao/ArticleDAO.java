package org.myftp.gattserver.grass3.articles.dao;

import org.myftp.gattserver.grass3.articles.domain.Article;
import org.myftp.gattserver.grass3.model.AbstractDAO;

public class ArticleDAO extends AbstractDAO<Article> {

	public ArticleDAO() {
		super(Article.class);
	}

}
