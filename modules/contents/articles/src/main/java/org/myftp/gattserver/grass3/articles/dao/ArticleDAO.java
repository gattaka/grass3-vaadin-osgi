package org.myftp.gattserver.grass3.articles.dao;

import org.hibernate.Transaction;
import org.myftp.gattserver.grass3.articles.domain.Article;
import org.myftp.gattserver.grass3.model.AbstractDAO;

public class ArticleDAO extends AbstractDAO<Article> {

	public ArticleDAO() {
		super(Article.class);
	}
	
	public boolean delete(Long nodeId) {
		Transaction tx = null;
		openSession();
		try {
			tx = session.beginTransaction();
			Article article = findByIdAndCast(entityClass, nodeId);			
			session.delete(article);
			tx.commit();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			if (tx != null) {
				tx.rollback();
			}
			return false;
		} finally {
			closeSession();
		}
	}

}
